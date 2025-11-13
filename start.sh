#!/bin/bash

# ============================================
# Script de Inicialização Rápida
# Projeto: Avaliação Outsera
# ============================================

clear

echo "╔════════════════════════════════════════════════╗"
echo "║  Avaliação Outsera - Inicialização Rápida     ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Função para pausar
pause() {
    echo ""
    read -p "Pressione ENTER para continuar..."
    echo ""
}

# Passo 1: Verificar Java
echo -e "${BLUE}[1/5]${NC} Verificando Java 17..."
if java -version 2>&1 | grep -q "17"; then
    echo -e "${GREEN}✅ Java 17 instalado${NC}"
else
    echo -e "${RED}❌ Java 17 não encontrado${NC}"
    echo "   Instale Java 17: https://adoptium.net/"
    exit 1
fi

# Passo 2: Verificar Maven
echo ""
echo -e "${BLUE}[2/5]${NC} Verificando Maven..."
if command -v mvn &> /dev/null; then
    echo -e "${GREEN}✅ Maven instalado${NC}"
    mvn --version | head -1
else
    echo -e "${RED}❌ Maven não encontrado${NC}"
    echo "   Instale Maven: brew install maven (macOS) ou apt install maven (Linux)"
    exit 1
fi

# Passo 3: Testar Conexão MySQL
echo ""
echo -e "${BLUE}[3/5]${NC} Testando conexão com MySQL..."
if ./test-mysql-connection.sh 2>/dev/null | grep -q "sucesso"; then
    echo -e "${GREEN}✅ MySQL conectado${NC}"
else
    echo -e "${YELLOW}⚠️  MySQL não acessível${NC}"
    echo ""
    echo "Deseja configurar o banco de dados agora? (s/n)"
    read -r resposta
    if [[ "$resposta" == "s" || "$resposta" == "S" ]]; then
        echo ""
        echo "Execute os comandos abaixo no MySQL como root:"
        echo ""
        cat setup-database.sql
        echo ""
        pause
    fi
fi

# Passo 4: Compilar o projeto
echo ""
echo -e "${BLUE}[4/5]${NC} Compilando o projeto..."
if mvn clean package -DskipTests -q; then
    echo -e "${GREEN}✅ Projeto compilado com sucesso${NC}"
else
    echo -e "${RED}❌ Erro na compilação${NC}"
    echo "   Execute: mvn clean package -DskipTests"
    exit 1
fi

# Passo 5: Instruções finais
echo ""
echo -e "${BLUE}[5/5]${NC} Pronto para executar!"
echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║           Como Executar a Aplicação           ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "1️⃣  Executar na porta padrão (8080):"
echo "   ${GREEN}mvn spring-boot:run${NC}"
echo ""
echo "2️⃣  Executar em porta customizada (8081):"
echo "   ${GREEN}mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081${NC}"
echo ""
echo "3️⃣  Executar JAR diretamente:"
echo "   ${GREEN}java -jar target/DeskInstaller-1.0-SNAPSHOT.jar${NC}"
echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║              Endpoints Disponíveis             ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "🌐 Página inicial:"
echo "   ${BLUE}http://localhost:8080/${NC}"
echo ""
echo "📊 API de Filmes:"
echo "   ${BLUE}http://localhost:8080/api/movies${NC}"
echo ""
echo "🎯 Intervalos de Produtores:"
echo "   ${BLUE}http://localhost:8080/api/movies/producers/intervals${NC}"
echo ""
echo "📖 Swagger UI:"
echo "   ${BLUE}http://localhost:8080/swagger-ui.html${NC}"
echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║                Testes da API                   ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "Execute o script de testes:"
echo "   ${GREEN}./test-api.sh${NC}"
echo ""
echo "Ou teste manualmente:"
echo "   ${GREEN}curl http://localhost:8080/api/movies${NC}"
echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║               Banco de Dados MySQL             ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "Host:     localhost:3306"
echo "Database: dk_db"
echo "User:     julioizidoro"
echo "DDL:      update (cria tabelas automaticamente)"
echo ""
echo "Verificar tabelas criadas:"
echo "   ${GREEN}mysql -u julioizidoro -p dk_db -e 'SHOW TABLES;'${NC}"
echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║              Documentação Criada               ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "📄 CONFIG-MYSQL.md       - Configuração MySQL detalhada"
echo "📄 MIGRACAO-JPA.md       - Migração Jakarta Persistence"
echo "📄 RESUMO-FINAL.md       - Resumo completo do projeto"
echo "📄 setup-database.sql    - Script de preparação do banco"
echo "📄 test-mysql-connection.sh - Teste de conexão MySQL"
echo "📄 test-api.sh           - Testes automatizados da API"
echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║   ✅ Tudo pronto! Boa sorte no desenvolvimento! ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════╝${NC}"
echo ""

