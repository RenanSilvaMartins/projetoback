#!/bin/bash

BASE_URL="http://localhost:8082"

echo "=== TESTANDO API DO PROJETO ==="
echo

# Função para testar endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo "🔍 Testando: $description"
    echo "   $method $BASE_URL$endpoint"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$endpoint" 2>/dev/null)
    else
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Content-Type: application/json" \
            "$BASE_URL$endpoint" 2>/dev/null)
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo "   ✅ Status: $http_code (Sucesso)"
    elif [ "$http_code" -ge 400 ] && [ "$http_code" -lt 500 ]; then
        echo "   ⚠️  Status: $http_code (Erro do Cliente)"
    elif [ "$http_code" -ge 500 ]; then
        echo "   ❌ Status: $http_code (Erro do Servidor)"
    else
        echo "   ❓ Status: $http_code"
    fi
    
    if [ ${#body} -gt 200 ]; then
        echo "   📄 Resposta: $(echo "$body" | head -c 200)..."
    else
        echo "   📄 Resposta: $body"
    fi
    echo
}

# Aguardar aplicação iniciar
echo "⏳ Aguardando aplicação iniciar..."
sleep 5

# Testar endpoints básicos
test_endpoint "GET" "/cliente" "" "Listar todos os clientes"
test_endpoint "GET" "/especialidade" "" "Listar todas as especialidades"
test_endpoint "GET" "/regiao" "" "Listar todas as regiões"
test_endpoint "GET" "/tecnico" "" "Listar todos os técnicos"
test_endpoint "GET" "/usuario" "" "Listar todos os usuários"

# Testar inicialização de dados padrão
test_endpoint "POST" "/especialidade/initialize" "" "Inicializar especialidades padrão"
test_endpoint "POST" "/regiao/initialize" "" "Inicializar regiões padrão"

# Testar endpoints com dados inválidos
test_endpoint "GET" "/cliente/999999" "" "Buscar cliente inexistente"
test_endpoint "POST" "/cliente" '{"cpf":"123"}' "Criar cliente com dados inválidos"

echo "=== TESTE CONCLUÍDO ==="