-- Recria a tabela `usuario` no formato que a entidade br.com.deskinstaller.model.Usuario
-- espera hoje (idusuario INT AUTO_INCREMENT, sem papeis, com idfuncionario opcional).
-- Rode com:  mysql -u SEU_USUARIO -p dk_db < recriar-usuario.sql

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS usuario (
    idusuario     INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(100) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    ativo         TINYINT(1)   NOT NULL DEFAULT 1,
    idfuncionario INT          NULL,
    PRIMARY KEY (idusuario),
    UNIQUE KEY uk_usuario_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- Tokens emitidos antes da recriacao apontam para usuarios que nao existem mais.
DELETE FROM refresh_token;

-- Usuario admin com senha "admin".
-- O hash tem o prefixo {bcrypt} exigido pelo PasswordEncoderFactories do projeto.
INSERT INTO usuario (username, password, ativo, idfuncionario)
SELECT 'admin',
       '{bcrypt}$2a$10$0O2VPZUB7cZMyI4J5EhwZOMfwLvg6kDbBthUTrPsiQ51RiLkxjpP2',
       1,
       NULL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'admin');

SELECT idusuario, username, ativo, idfuncionario FROM usuario;
