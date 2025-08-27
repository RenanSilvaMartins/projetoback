#!/bin/bash

# Script para configurar e executar a aplicação em diferentes perfis
# ./run-profiles.sh [perfil] onde perfil pode ser: dev, test, prod

PROFILE=${1:-dev}

echo "=== Configurando ambiente para perfil: $PROFILE ==="

case $PROFILE in
    "dev")
        echo "🔧 Iniciando em modo DESENVOLVIMENTO"
        echo "   - Banco H2 em memória"
        echo "   - Console H2 habilitado"
        echo "   - Logs detalhados"
        echo "   - Actuator completo exposto"
        mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE
        ;;
    "test")
        echo "🧪 Executando TESTES"
        echo "   - Banco H2 para testes"
        echo "   - Logs reduzidos"
        echo "   - Métricas básicas"
        mvn test -Dspring.profiles.active=$PROFILE
        ;;
    "prod")
        echo "🚀 Iniciando em modo PRODUÇÃO"
        echo "   - Banco SQL Server"
        echo "   - Logs otimizados"
        echo "   - Segurança reforçada"
        echo "   - Atuator em porta separada"
        mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE
        ;;
    *)
        echo "❌ Perfil inválido: $PROFILE"
        echo "Perfis disponíveis: dev, test, prod"
        echo "Exemplo: ./run-profiles.sh dev"
        exit 1
        ;;
esac
