#!/bin/bash

# ============================================
# Script para executar GRANT ALL no MySQL
# ============================================

echo "╔════════════════════════════════════════════════╗"
echo "║   Configurando Usuario MySQL - julioizidoro    ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Verificar se MySQL está instalado
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL não encontrado!"
    echo "   Instale com: brew install mysql"
    exit 1
fi

echo "✅ MySQL encontrado"
echo ""

# Verificar se MySQL está rodando
if ! pgrep -x mysqld > /dev/null; then
    echo "⚠️  MySQL não está rodando"
    echo "   Iniciando MySQL..."
    brew services start mysql
    sleep 3
fi

echo "✅ MySQL está rodando"
echo ""

echo "📝 Executando script SQL..."
echo "   Digite a senha do ROOT do MySQL quando solicitado"
echo ""

# Executar o script SQL
mysql -u root -p < grant-user-mysql.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "╔════════════════════════════════════════════════╗"
    echo "║          ✅ Usuario Configurado!               ║"
    echo "╚════════════════════════════════════════════════╝"
    echo ""
    echo "📊 Configuração:"
    echo "   Database: dk_db"
    echo "   Usuario:  julioizidoro"
    echo "   Senha:    20SimpleS78**"
    echo "   Host:     localhost"
    echo ""
    echo "🧪 Testando conexão..."
    mysql -u julioizidoro -p'20SimpleS78**' dk_db -e "SELECT 'Conexao OK!' AS Status;" 2>/dev/null

    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ SUCESSO! Usuario julioizidoro pode acessar dk_db"
        echo ""
        echo "🚀 Próximo passo: Execute a aplicação"
        echo "   mvn spring-boot:run"
    else
        echo ""
        echo "⚠️  Erro ao testar conexão"
        echo "   Verifique se a senha está correta"
    fi
else
    echo ""
    echo "❌ Erro ao executar script SQL"
    echo "   Verifique a senha do root"
fi

echo ""

