package br.com.deskinstaller.service;

import br.com.deskinstaller.model.RefreshToken;
import br.com.deskinstaller.model.Usuario;
import br.com.deskinstaller.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.refresh-token.expiration-seconds:604800}")
    private long refreshTokenExpirationSeconds;

    @Transactional
    public RefreshToken issueToken(Usuario usuario) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateOpaqueToken());
        refreshToken.setUsuario(usuario);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Refresh token inválido"));

        if (refreshToken.isRevoked()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token inválido");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token expirado");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotate(RefreshToken refreshToken) {
        revoke(refreshToken);
        return issueToken(refreshToken.getUsuario());
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
