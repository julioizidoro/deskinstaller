#!/bin/bash

# Script de teste de conexão MySQL
# Database: dk_db

echo "=================================================="
echo "  Teste de Conexão MySQL - dk_db"
echo "=================================================="
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="dk_db"
DB_USER="julioizidoro"
DB_PASS="20SimpleS78**"

echo "1️⃣  Verificando se o MySQL está rodando..."
if command -v mysql &> /dev/null; then
    echo -e "${GREEN}✅ MySQL client instalado${NC}"
else
    echo -e "${RED}❌ MySQL client não encontrado${NC}"
    echo "   Instale com: brew install mysql (macOS) ou apt install mysql-client (Linux)"
    exit 1
fi

echo ""
echo "2️⃣  Testando conexão TCP na porta 3306..."
if nc -z -w5 $DB_HOST $DB_PORT 2>/dev/null; then
    echo -e "${GREEN}✅ Porta 3306 acessível${NC}"
else
    echo -e "${RED}❌ Porta 3306 não responde${NC}"
    echo "   Verifique se o MySQL está rodando:"
    echo "   - macOS: brew services list"
    echo "   - Linux: sudo systemctl status mysql"
    exit 1
fi

echo ""
echo "3️⃣  Testando autenticação no banco dk_db..."
if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null; then
    echo -e "${GREEN}✅ Conexão autenticada com sucesso!${NC}"
else
    echo -e "${RED}❌ Falha na autenticação${NC}"
    echo "   Verifique:"
    echo "   - Usuário: $DB_USER"
    echo "   - Senha: (conferir application.properties)"
    echo "   - Database: $DB_NAME"
    echo ""
    echo "   Execute o script de setup:"
    echo "   mysql -u root -p < setup-database.sql"
    exit 1
fi

echo ""
echo "4️⃣  Verificando tabelas existentes..."
TABLE_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" -se "SHOW TABLES;" 2>/dev/null | wc -l)
echo "   Tabelas encontradas: $TABLE_COUNT"

if [ "$TABLE_COUNT" -gt 0 ]; then
    echo ""
    echo "   Tabelas:"
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" -se "SHOW TABLES;" 2>/dev/null | while read table; do
        echo "   - $table"
    done
else
    echo -e "${YELLOW}   ⚠️  Nenhuma tabela criada ainda${NC}"
    echo "   Execute a aplicação Spring Boot para criar as tabelas automaticamente."
fi

echo ""
echo "5️⃣  Informações do banco..."
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" -e "
SELECT
    DATABASE() as 'Database Atual',
    @@character_set_database as 'Charset',
    @@collation_database as 'Collation',
    @@version as 'MySQL Version';
" 2>/dev/null

echo ""
echo "=================================================="
echo -e "${GREEN}✅ Teste de conexão concluído com sucesso!${NC}"
echo "=================================================="
echo ""
echo "📝 Próximo passo: Executar a aplicação Spring Boot"
echo "   mvn spring-boot:run"
echo ""

