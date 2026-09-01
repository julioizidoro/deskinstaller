package br.com.deskinstaller.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Emissao e validacao dos tokens JWT da API.
 *
 * <p>O segredo de assinatura NAO tem valor padrao embutido no codigo. Em producao
 * ele e obrigatorio; fora de producao, quando ausente, uma chave aleatoria e
 * gerada apenas para a execucao corrente (todos os tokens sao invalidados a cada
 * restart), o que evita que um segredo conhecido publicamente assine tokens reais.
 */
@Service
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    /** Tamanho minimo da chave HMAC-SHA256, em bytes, exigido pela RFC 7518. */
    private static final int MIN_KEY_BYTES = 32;

    private final Environment environment;

    @Value("${app.security.jwt.secret:}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration-seconds:14400}")
    private long expirationSeconds;

    private SecretKey signingKey;

    public JwtTokenService(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    void init() {
        boolean production = environment.acceptsProfiles(Profiles.of("prod"));

        if (jwtSecret == null || jwtSecret.isBlank()) {
            if (production) {
                throw new IllegalStateException(
                        "app.security.jwt.secret (APP_SECURITY_JWT_SECRET) e obrigatorio em producao.");
            }
            signingKey = Keys.hmacShaKeyFor(gerarChaveEfemera());
            log.warn("APP_SECURITY_JWT_SECRET nao definido. Uma chave aleatoria foi gerada para esta execucao: "
                    + "todos os tokens serao invalidados no proximo restart. Defina o segredo no .env.");
            return;
        }

        byte[] material = decodificarSegredo(jwtSecret);
        if (material.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("app.security.jwt.secret precisa resultar em ao menos " + MIN_KEY_BYTES
                    + " bytes (256 bits). Gere um valor adequado com: openssl rand -base64 48");
        }
        signingKey = Keys.hmacShaKeyFor(material);
    }

    private byte[] gerarChaveEfemera() {
        byte[] material = new byte[MIN_KEY_BYTES];
        new java.security.SecureRandom().nextBytes(material);
        return material;
    }

    /**
     * Aceita o segredo em base64 padrao, base64url (que usa '-' e '_' no lugar
     * de '+' e '/') ou como texto puro. O texto puro e util para quem gera o
     * segredo com um gerenciador de senhas em vez de openssl, e continua sujeito
     * a exigencia de tamanho minimo aplicada no init().
     */
    private byte[] decodificarSegredo(String segredo) {
        String limpo = segredo.trim();
        try {
            return Decoders.BASE64.decode(limpo);
        } catch (IllegalArgumentException ignorado) {
            // segue para as proximas tentativas
        }
        try {
            return Decoders.BASE64URL.decode(limpo);
        } catch (IllegalArgumentException ignorado) {
            // segue para o texto puro
        }
        log.warn("app.security.jwt.secret nao esta em base64; sera usado como texto puro. "
                + "Prefira um valor gerado com: openssl rand -base64 48");
        return limpo.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
