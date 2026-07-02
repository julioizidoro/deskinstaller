#!/bin/bash

set -e

if [ -f ".env" ]; then
  set -a
  . ./.env
  set +a
fi

echo "DeskInstaller API"
echo

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven nao encontrado."
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java nao encontrado."
  exit 1
fi

echo "Java:"
java -version 2>&1 | head -n 1
echo

echo "Maven:"
mvn -version | head -n 1
echo

if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ]; then
  echo "DB_USERNAME ou DB_PASSWORD nao definidos."
  echo "Preencha o arquivo .env ou use as variaveis de ambiente documentadas no README."
  exit 1
fi

if [ -z "${APP_SECURITY_JWT_SECRET}" ]; then
  echo "APP_SECURITY_JWT_SECRET nao definido."
  echo "Preencha o arquivo .env com um segredo JWT em base64."
  exit 1
fi

echo "Compilando projeto..."
mvn clean package -DskipTests
echo

echo "Executando aplicacao..."
echo "Swagger: http://localhost:8080/swagger-ui.html"
echo "API: http://localhost:8080"
echo "Profile: ${SPRING_PROFILES_ACTIVE:-dev}"
echo

SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}" java -jar target/deskinstaller-api-1.0.0-SNAPSHOT.jar
