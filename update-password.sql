-- Script para ATUALIZAR a senha do usuario julioizidoro
-- Execute: mysql -u root -p < update-password.sql

-- Atualizar senha do usuario para a senha do application.properties
ALTER USER 'julioizidoro'@'localhost' IDENTIFIED BY '20SimpleS78**';

-- Aplicar mudancas
FLUSH PRIVILEGES;

-- Verificar
SELECT 'Senha atualizada com sucesso!' AS Status;
SHOW GRANTS FOR 'julioizidoro'@'localhost';

