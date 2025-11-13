-- ============================================
-- Script de Preparação do Banco de Dados MySQL
-- Database: dk_db
-- User: julioizidoro
-- ============================================

-- Criar o banco de dados se não existir
CREATE DATABASE IF NOT EXISTS dk_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE dk_db;

-- Criar usuário se não existir (MySQL 8.0+)
CREATE USER IF NOT EXISTS 'julioizidoro'@'localhost'
    IDENTIFIED BY '20SimpleS78**';

-- Conceder todas as permissões no banco dk_db
GRANT ALL PRIVILEGES ON dk_db.* TO 'julioizidoro'@'localhost';

-- Aplicar as mudanças
FLUSH PRIVILEGES;

-- Verificações
SELECT 'Database criado com sucesso!' AS Status;
SHOW DATABASES LIKE 'dk_db';

SELECT 'Usuario criado com sucesso!' AS Status;
SELECT User, Host FROM mysql.user WHERE User = 'julioizidoro';

SELECT 'Permissoes concedidas!' AS Status;
SHOW GRANTS FOR 'julioizidoro'@'localhost';

-- Informações do banco
SELECT
    'Configuracao do Banco' AS Info,
    @@character_set_database AS CharSet,
    @@collation_database AS Collation,
    @@version AS MySQLVersion;

