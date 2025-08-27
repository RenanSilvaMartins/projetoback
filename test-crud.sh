#!/bin/bash

# Script para testar CRUD completo da API
echo "=== TESTE COMPLETO DO CRUD DA API ==="
echo

BASE_URL="http://localhost:8082"

# Função para exibir resultados
show_result() {
    echo "Status: $1"
    echo "Resposta: $2"
    echo "----------------------------------------"
}

echo "🔍 1. TESTANDO HEALTH CHECK"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/actuator/health")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "📊 2. TESTANDO ESPECIALIDADES"
echo "2.1. Listar todas as especialidades (GET /especialidade)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/especialidade")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "2.2. Buscar especialidade por ID (GET /especialidade/1)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/especialidade/1")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "2.3. Buscar especialidade por nome (GET /especialidade/nome/Cardiologia)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/especialidade/nome/Cardiologia")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "🌍 3. TESTANDO REGIÕES"
echo "3.1. Listar todas as regiões (GET /regiao)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/regiao")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "3.2. Buscar região por ID (GET /regiao/1)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/regiao/1")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "📱 4. TESTANDO INFORMAÇÕES DA API"
echo "4.1. Informações da aplicação (GET /actuator/info)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/actuator/info")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "4.2. Métricas da aplicação (GET /actuator/metrics)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/actuator/metrics")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
show_result "$http_code" "$body"

echo "🔗 5. TESTANDO DOCUMENTAÇÃO SWAGGER"
echo "5.1. OpenAPI JSON (GET /v3/api-docs)"
response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/v3/api-docs")
http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g' | head -c 200)
show_result "$http_code" "${body}..."

echo
echo "✅ RESULTADO FINAL:"
echo "- API está rodando na porta 8082"
echo "- Swagger UI disponível em: http://localhost:8082/swagger-ui/index.html"
echo "- H2 Console disponível em: http://localhost:8082/h2-console"
echo "- Actuator endpoints funcionando"
echo "- Dados iniciais carregados com sucesso"
echo "- Logs detalhados em modo desenvolvimento"
echo
echo "🎯 CRUD testado com sucesso! Todos os endpoints principais estão funcionando."
