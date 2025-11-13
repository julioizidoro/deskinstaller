#!/bin/bash

# Script de teste da API - Golden Raspberry Awards
# Uso: ./test-api.sh [porta]

PORT=${1:-8080}
BASE_URL="http://localhost:${PORT}"

echo "=================================================="
echo "  Teste da API - Golden Raspberry Awards"
echo "  Base URL: ${BASE_URL}"
echo "=================================================="
echo ""

# Teste 1: Página inicial
echo "1️⃣  Testando página inicial..."
curl -s -o /dev/null -w "Status: %{http_code}\n" ${BASE_URL}/
echo ""

# Teste 2: Listar todos os filmes
echo "2️⃣  Testando GET /api/movies (primeiros 5 filmes)..."
curl -s ${BASE_URL}/api/movies | head -50
echo ""
echo ""

# Teste 3: Buscar filme por ID
echo "3️⃣  Testando GET /api/movies/1..."
curl -s ${BASE_URL}/api/movies/1
echo ""
echo ""

# Teste 4: Intervalos de produtores
echo "4️⃣  Testando GET /api/movies/producers/intervals..."
curl -s ${BASE_URL}/api/movies/producers/intervals | python3 -m json.tool 2>/dev/null || curl -s ${BASE_URL}/api/movies/producers/intervals
echo ""
echo ""

# Teste 5: OPTIONS
echo "5️⃣  Testando OPTIONS /api/movies..."
curl -s -X OPTIONS -i ${BASE_URL}/api/movies | grep -E "^(HTTP|Allow):"
echo ""

# Teste 6: 404
echo "6���⃣  Testando 404 (filme inexistente)..."
curl -s -w "\nHTTP Status: %{http_code}\n" ${BASE_URL}/api/movies/999999
echo ""

# Teste 7: Console H2
echo "7️⃣  Testando H2 Console..."
curl -s -o /dev/null -w "H2 Console Status: %{http_code}\n" ${BASE_URL}/h2-console
echo ""

echo "=================================================="
echo "  ✅ Testes concluídos!"
echo "=================================================="

