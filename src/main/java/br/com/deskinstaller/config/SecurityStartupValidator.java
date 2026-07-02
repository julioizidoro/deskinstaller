package br.com.deskinstaller.config;

import jakarta.annotation.PostConstruct;
import br.com.deskinstaller.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class SecurityStartupValidator {

    private final Environment environment;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    @Value("${app.security.public-docs-enabled:true}")
    private boolean publicDocsEnabled;

    @Value("${app.security.jwt.secret:ZGVza2luc3RhbGxlci1kZXYtamF3dC1zZWNyZXQtdmVyeS1sb25nLTEyMzQ1Njc4OTA=}")
    private String jwtSecret;

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
            throw new IllegalStateException("Em produção, app.security.enabled não pode ser false.");
        }
        if (usuarioRepository.count() == 0) {
            throw new IllegalStateException("Em produção, deve existir ao menos um usuário cadastrado no banco.");
        }
        if (publicDocsEnabled) {
            throw new IllegalStateException("Em produção, app.security.public-docs-enabled deve ser false.");
        }
        if (jwtSecret == null || jwtSecret.isBlank() || jwtSecret.length() < 32) {
            throw new IllegalStateException("Em produção, app.security.jwt.secret deve estar definido com ao menos 32 caracteres base64.");
        }
    }
}
