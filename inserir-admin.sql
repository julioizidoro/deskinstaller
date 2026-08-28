-- Insere (ou atualiza) o usuario "admin" com a senha "admin" ja criptografada.
-- Rode com:  mysql -u SEU_USUARIO -p dk_db < inserir-admin.sql
--
-- A senha NAO vai em texto puro: o valor abaixo e um hash BCrypt (custo 10)
-- com o prefixo {bcrypt} exigido pelo PasswordEncoderFactories do projeto.
-- Sem esse prefixo o Spring Security nao reconhece o algoritmo e o login falha.

INSERT INTO usuario (username, password, ativo, idfuncionario)
VALUES ('admin',
        '{bcrypt}$2a$10$0O2VPZUB7cZMyI4J5EhwZOMfwLvg6kDbBthUTrPsiQ51RiLkxjpP2',
        1,
        NULL) AS novo
ON DUPLICATE KEY UPDATE
    password = novo.password,
    ativo    = 1;

SELECT idusuario, username, ativo, idfuncionario FROM usuario WHERE username = 'admin';
