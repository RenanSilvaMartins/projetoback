# 🚀 Guia de Deploy e Performance

[![Deploy](https://img.shields.io/badge/Deploy-Ready-green.svg)]()
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)]()
[![Cloud](https://img.shields.io/badge/Cloud-Ready-orange.svg)]()

Este guia cobre estratégias de deploy, otimizações de performance e configurações para diferentes ambientes.

## 📋 Checklist Pré-Deploy

### ✅ Desenvolvimento
- [ ] Todos os testes passando
- [ ] Cobertura de testes > 80%
- [ ] Logs configurados adequadamente
- [ ] Variáveis de ambiente documentadas
- [ ] Documentação atualizada
- [ ] Performance testada
- [ ] Segurança validada

### ✅ Produção
- [ ] Certificados SSL configurados
- [ ] Banco de dados de produção
- [ ] Backup automatizado
- [ ] Monitoramento ativo
- [ ] Alertas configurados
- [ ] DNS configurado
- [ ] CDN configurado (se aplicável)

## 🐳 Deploy com Docker

### Dockerfile Otimizado
```dockerfile
# Multi-stage build para otimização
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Download de dependências (cacheable layer)
RUN ./mvnw dependency:go-offline -B

# Build da aplicação
RUN ./mvnw clean package -DskipTests

# Estágio de produção
FROM eclipse-temurin:21-jre-alpine

# Criar usuário não-root para segurança
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser

# Instalar dependências do sistema
RUN apk add --no-cache curl

WORKDIR /app

# Copiar JAR do estágio builder
COPY --from=builder /app/target/*.jar app.jar

# Configurar propriedades de JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Mudar para usuário não-root
USER appuser

EXPOSE 8082

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose para Desenvolvimento
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
    volumes:
      - ./logs:/app/logs
    depends_on:
      - database
    networks:
      - app-network

  database:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      SA_PASSWORD: "YourPassword123!"
      ACCEPT_EULA: "Y"
    ports:
      - "1433:1433"
    volumes:
      - db_data:/var/opt/mssql
    networks:
      - app-network

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - app-network

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - app-network

volumes:
  db_data:
  grafana_data:

networks:
  app-network:
    driver: bridge
```

### Docker Compose para Produção
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  app:
    image: sistema-gerenciamento:latest
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=${DATABASE_URL}
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - SENDGRID_API_KEY=${SENDGRID_API_KEY}
    volumes:
      - ./logs:/app/logs
      - ./uploads:/app/uploads
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8082/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    networks:
      - prod-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - prod-network

networks:
  prod-network:
    driver: bridge
```

## 🌐 Deploy em Cloud

### AWS (Elastic Beanstalk)
```yaml
# .ebextensions/01-java.config
option_settings:
  aws:elasticbeanstalk:container:java:
    Xmx: 512m
    Xms: 256m
    JVMOptions: "-XX:+UseG1GC -XX:+UseContainerSupport"
  aws:elasticbeanstalk:application:environment:
    SPRING_PROFILES_ACTIVE: prod
    SERVER_PORT: 5000
```

### Heroku
```yaml
# Procfile
web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar
```

```json
// app.json
{
  "name": "sistema-gerenciamento",
  "description": "API REST para gerenciamento",
  "keywords": ["java", "spring-boot", "api"],
  "env": {
    "SPRING_PROFILES_ACTIVE": {
      "value": "prod"
    },
    "JWT_SECRET": {
      "generator": "secret"
    }
  },
  "addons": [
    "heroku-postgresql:mini"
  ]
}
```

### Google Cloud Platform (Cloud Run)
```yaml
# cloudbuild.yaml
steps:
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/$PROJECT_ID/sistema-gerenciamento', '.']
  
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'gcr.io/$PROJECT_ID/sistema-gerenciamento']
    
  - name: 'gcr.io/cloud-builders/gcloud'
    args:
    - 'run'
    - 'deploy'
    - 'sistema-gerenciamento'
    - '--image=gcr.io/$PROJECT_ID/sistema-gerenciamento'
    - '--region=us-central1'
    - '--platform=managed'
    - '--allow-unauthenticated'
```

## ⚡ Otimizações de Performance

### Configurações JVM
```bash
# Para produção
JAVA_OPTS="-server \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/logs/ \
  -XX:+UseCompressedOops \
  -XX:+UseStringDeduplication"
```

### Configurações do Spring Boot
```properties
# application-prod.properties

# Pool de conexões otimizado
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.leak-detection-threshold=60000

# Cache
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=30m

# JSON
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.default-property-inclusion=NON_NULL

# Thread pool
spring.task.execution.pool.core-size=8
spring.task.execution.pool.max-size=32
spring.task.execution.pool.queue-capacity=1000

# Actuator apenas para saúde em produção
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=never
```

### Configurações do Hibernate
```properties
# Otimizações Hibernate
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
spring.jpa.properties.hibernate.generate_statistics=false
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.caffeine.CaffeineCacheRegionFactory
```

## 📊 Monitoramento

### Prometheus Configuration
```yaml
# monitoring/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['app:8082']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
```

### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "Sistema Gerenciamento - Métricas",
    "panels": [
      {
        "title": "Requests per Second",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, http_request_duration_seconds_bucket)"
          }
        ]
      }
    ]
  }
}
```

### Alertas (AlertManager)
```yaml
# alerts.yml
groups:
  - name: application
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Alta taxa de erro na aplicação"

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, http_request_duration_seconds_bucket) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Tempo de resposta alto"
```

## 🔄 CI/CD Pipeline

### GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Cache Maven dependencies
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          
      - name: Run tests
        run: ./mvnw test
        
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: success() || failure()
        with:
          name: Maven Tests
          path: target/surefire-reports/*.xml
          reporter: java-junit

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: docker build -t sistema-gerenciamento:${{ github.sha }} .
        
      - name: Deploy to production
        run: |
          # Deploy script aqui
          echo "Deploying to production..."
```

### GitLab CI
```yaml
# .gitlab-ci.yml
stages:
  - test
  - build
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

cache:
  paths:
    - .m2/repository

test:
  stage: test
  image: openjdk:21-jdk
  script:
    - ./mvnw test
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml

build:
  stage: build
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

deploy:
  stage: deploy
  script:
    - kubectl set image deployment/sistema-gerenciamento app=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  only:
    - main
```

## 🔧 Scripts de Deploy

### Script de Deploy Automatizado
```bash
#!/bin/bash
# deploy.sh

set -e

echo "🚀 Iniciando deploy..."

# Verificar se está na branch main
if [[ $(git branch --show-current) != "main" ]]; then
    echo "❌ Deploy deve ser feito a partir da branch main"
    exit 1
fi

# Executar testes
echo "🧪 Executando testes..."
./mvnw clean test

# Build da aplicação
echo "🔨 Fazendo build..."
./mvnw package -DskipTests

# Build da imagem Docker
echo "🐳 Construindo imagem Docker..."
docker build -t sistema-gerenciamento:latest .

# Tag para registry
docker tag sistema-gerenciamento:latest registry.empresa.com/sistema-gerenciamento:latest

# Push para registry
echo "📤 Enviando para registry..."
docker push registry.empresa.com/sistema-gerenciamento:latest

# Deploy no Kubernetes
echo "☸️ Fazendo deploy no Kubernetes..."
kubectl set image deployment/sistema-gerenciamento app=registry.empresa.com/sistema-gerenciamento:latest

# Aguardar rollout
kubectl rollout status deployment/sistema-gerenciamento

echo "✅ Deploy concluído com sucesso!"
```

### Script de Rollback
```bash
#!/bin/bash
# rollback.sh

set -e

echo "🔄 Iniciando rollback..."

# Fazer rollback no Kubernetes
kubectl rollout undo deployment/sistema-gerenciamento

# Aguardar rollback
kubectl rollout status deployment/sistema-gerenciamento

echo "✅ Rollback concluído!"
```

## 📈 Métricas de Performance

### KPIs Importantes
- **Throughput**: Requisições por segundo
- **Latência**: Tempo médio de resposta
- **Uptime**: Disponibilidade do serviço
- **Error Rate**: Taxa de erro
- **Memory Usage**: Uso de memória
- **CPU Usage**: Uso de CPU
- **Database Performance**: Tempo de queries

### Configuração de Métricas Customizadas
```java
@Component
public class CustomMetrics {
    
    private final Counter userRegistrations;
    private final Timer loginTime;
    private final Gauge activeUsers;
    
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.userRegistrations = Counter.builder("user.registrations")
            .description("Total de registros de usuários")
            .register(meterRegistry);
            
        this.loginTime = Timer.builder("user.login.time")
            .description("Tempo de login de usuários")
            .register(meterRegistry);
            
        this.activeUsers = Gauge.builder("user.active")
            .description("Usuários ativos")
            .register(meterRegistry, this, CustomMetrics::getActiveUserCount);
    }
    
    public void incrementUserRegistrations() {
        userRegistrations.increment();
    }
    
    public Timer.Sample startLoginTimer() {
        return Timer.start(Clock.SYSTEM);
    }
    
    public void recordLoginTime(Timer.Sample sample) {
        sample.stop(loginTime);
    }
    
    private double getActiveUserCount() {
        // Implementar lógica para contar usuários ativos
        return 0.0;
    }
}
```

## 🏥 Health Checks

### Health Check Customizado
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try {
            // Verificar conexão com banco
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                
                if (isValid) {
                    return Health.up()
                        .withDetail("database", "Connected")
                        .withDetail("connection_pool", getPoolStatus())
                        .build();
                } else {
                    return Health.down()
                        .withDetail("database", "Connection invalid")
                        .build();
                }
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "Connection failed")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
    
    private Map<String, Object> getPoolStatus() {
        // Implementar status do pool de conexões
        return Map.of(
            "active", 5,
            "idle", 3,
            "max", 20
        );
    }
}
```

---

**📌 Nota**: Sempre teste as configurações em ambiente de desenvolvimento antes de aplicar em produção.
