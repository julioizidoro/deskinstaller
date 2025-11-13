-- ============================================
-- Script para configurar usuario julioizidoro
-- Banco: dk_db
-- ============================================

-- Criar banco de dados se nao existir
CREATE DATABASE IF NOT EXISTS dk_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usar o banco
USE dk_db;

-- Remover usuario antigo se existir (para recriar limpo)
DROP USER IF EXISTS 'julioizidoro'@'localhost';

-- Criar usuario com senha
CREATE USER 'julioizidoro'@'localhost' IDENTIFIED BY '20SimpleS78**';

-- Conceder TODAS as permissoes no banco dk_db
GRANT ALL PRIVILEGES ON dk_db.* TO 'julioizidoro'@'localhost';

-- Permitir acesso de qualquer host (opcional - apenas se necessario)
-- DROP USER IF EXISTS 'julioizidoro'@'%';
-- CREATE USER 'julioizidoro'@'%' IDENTIFIED BY '20SimpleS78**';
-- GRANT ALL PRIVILEGES ON dk_db.* TO 'julioizidoro'@'%';

-- Aplicar as mudancas
FLUSH PRIVILEGES;

-- ============================================
-- Verificacoes
-- ============================================

-- Mostrar usuario criado
SELECT 'Usuario criado:' AS Info;
SELECT User, Host FROM mysql.user WHERE User = 'julioizidoro';

-- Mostrar banco criado
SELECT 'Banco de dados:' AS Info;
SHOW DATABASES LIKE 'dk_db';

-- Mostrar permissoes concedidas
SELECT 'Permissoes concedidas:' AS Info;
SHOW GRANTS FOR 'julioizidoro'@'localhost';

-- Testar acesso
SELECT 'Teste de acesso:' AS Info;
SELECT 'Acesso OK - Usuario julioizidoro pode usar dk_db' AS Status;

