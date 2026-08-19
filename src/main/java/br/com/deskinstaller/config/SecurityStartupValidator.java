package br.com.deskinstaller.config;

import br.com.deskinstaller.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Barreira de subida do perfil de producao: falha rapido quando a configuracao
 * de seguranca esta fraca, em vez de deixar a API subir exposta.
 */
@Configuration
public class SecurityStartupValidator {

    /**
     * Segredos que ja circularam no repositorio ou na documentacao e, portanto,
     * devem ser considerados publicos.
     */
    private static final Set<String> SEGREDOS_CONHECIDOS = Set.of(
            "ZGVza2luc3RhbGxlci1kZXYtamF3dC1zZWNyZXQtdmVyeS1sb25nLTEyMzQ1Njc4OTA=",
            "ZGVza2luc3RhbGxlci10ZXN0LWp3dC1zZWNyZXQtdmVyeS1sb25nLWFhYWFhYWFhYWE="
    );

    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final Environment environment;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    @Value("${app.security.public-docs-enabled:true}")
    private boolean publicDocsEnabled;

    @Value("${app.security.jwt.secret:}")
    private String jwtSecret;

    @Value("${app.cors.allowed-origins:}")
    private String corsAllowedOrigins;

    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    public SecurityStartupValidator(Environment environment, UsuarioRepository usuarioRepository) {
        this.environment = environment;
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    void validateProductionSettings() {
        if (!environment.acceptsProfiles(Profiles.of("prod"))) {
            return;
        }

        if (!securityEnabled) {
            throw new IllegalStateException("Em producao, app.security.enabled nao pode ser false.");
        }
        if (usuarioRepository.count() == 0) {
            throw new IllegalStateException("Em producao, deve existir ao menos um usuario cadastrado no banco.");
        }
        if (publicDocsEnabled) {
            throw new IllegalStateException("Em producao, app.security.public-docs-enabled deve ser false.");
        }
        validarSegredoJwt();
        validarCors();
        validarDdlAuto();
    }

    private void validarSegredoJwt() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "Em producao, app.security.jwt.secret (APP_SECURITY_JWT_SECRET) deve estar definido.");
        }
        if (jwtSecret.length() < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException("Em producao, app.security.jwt.secret deve ter ao menos "
                    + TAMANHO_MINIMO_SEGREDO + " caracteres base64.");
        }
        if (SEGREDOS_CONHECIDOS.contains(jwtSecret.trim())) {
            throw new IllegalStateException("Em producao, app.security.jwt.secret nao pode reutilizar um segredo "
                    + "de exemplo do repositorio. Gere um novo com: openssl rand -base64 48");
        }
    }

    private void validarCors() {
        List<String> origens = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toList();

        if (origens.contains("*")) {
            throw new IllegalStateException(
                    "Em producao, app.cors.allowed-origins nao pode ser '*'. Liste as origens explicitamente.");
        }
        boolean apenasLocalhost = !origens.isEmpty()
                && origens.stream().allMatch(origem -> origem.contains("localhost") || origem.contains("127.0.0.1"));
        if (apenasLocalhost) {
            throw new IllegalStateException(
                    "Em producao, app.cors.allowed-origins ainda aponta apenas para localhost. "
                            + "Defina APP_CORS_ALLOWED_ORIGINS com as origens reais do front-end.");
        }
    }

    private void validarDdlAuto() {
        if (!"validate".equalsIgnoreCase(ddlAuto) && !"none".equalsIgnoreCase(ddlAuto)) {
            throw new IllegalStateException("Em producao, spring.jpa.hibernate.ddl-auto deve ser 'validate' ou 'none', "
                    + "mas esta como '" + ddlAuto + "'. Use Flyway para alterar o schema.");
        }
    }
}
