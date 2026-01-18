#!/bin/bash

# Script para resolver erro: java.lang.ExceptionInInitializerError com.sun.tools.javac.code.TypeTag

echo "🔧 Resolvendo erro de compilação TypeTag..."

cd /Users/julioizidoro/Git/deskInstalller-api

# 1. Verificar versão do Java
echo ""
echo "1️⃣ Verificando versão do Java..."
java -version
javac -version

# 2. Limpar completamente o projeto
echo ""
echo "2️⃣ Limpando projeto..."
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/org/projectlombok/lombok/

# 3. Invalidar cache do Maven
echo ""
echo "3️⃣ Invalidando cache do Maven..."
mvn dependency:purge-local-repository -DmanualInclude=org.projectlombok:lombok

# 4. Baixar dependências novamente
echo ""
echo "4️⃣ Baixando dependências..."
mvn dependency:resolve

# 5. Compilar
echo ""
echo "5️⃣ Compilando projeto..."
mvn compile

echo ""
echo "✅ Processo concluído!"
echo ""
echo "Se ainda houver erro, execute:"
echo "  mvn clean install -U -DskipTests"

