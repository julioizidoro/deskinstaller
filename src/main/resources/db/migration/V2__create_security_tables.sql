CREATE TABLE IF NOT EXISTS role (
    idrole BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuario (
    idusuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuario_role (
    usuario_idusuario BIGINT NOT NULL,
    role_idrole BIGINT NOT NULL,
    PRIMARY KEY (usuario_idusuario, role_idrole),
    CONSTRAINT fk_usuario_role_usuario
        FOREIGN KEY (usuario_idusuario) REFERENCES usuario (idusuario),
    CONSTRAINT fk_usuario_role_role
        FOREIGN KEY (role_idrole) REFERENCES role (idrole)
);
