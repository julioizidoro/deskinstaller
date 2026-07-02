CREATE TABLE IF NOT EXISTS refresh_token (
    idrefresh_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_idusuario BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP NULL,
    CONSTRAINT fk_refresh_token_usuario
        FOREIGN KEY (usuario_idusuario) REFERENCES usuario (idusuario)
);
