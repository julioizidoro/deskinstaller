CREATE TABLE IF NOT EXISTS refresh_token (
    idrefresh_token INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuarioidusuario INT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP NULL,
    CONSTRAINT fk_refresh_token_usuario
        FOREIGN KEY (usuarioidusuario) REFERENCES usuario (idusuario)
);
