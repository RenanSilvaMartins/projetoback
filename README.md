# Sistema de Gerenciamento - Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()

API REST robusta e moderna desenvolvida com Spring Boot para gerenciamento completo de usuários, clientes, técnicos, especialidades e regiões. Projetada para integração com frontends em Flutter e ReactJS.

## 🚀 Tecnologias e Frameworks

### Core
- **Java 21** - Linguagem de programação com recursos mais recentes
- **Spring Boot 3.4.0** - Framework principal para desenvolvimento de aplicações
- **Spring Security 6** - Autenticação e autorização
- **Spring Data JPA** - Acesso a dados e mapeamento objeto-relacional
- **Hibernate 6** - Implementação JPA

### Banco de Dados
- **H2 Database** - Banco em memória para desenvolvimento
- **SQL Server** - Banco de dados para produção
- **HikariCP** - Pool de conexões de alta performance

### Documentação e Monitoramento
- **OpenAPI 3/Swagger** - Documentação interativa da API
- **Spring Boot Actuator** - Endpoints de saúde e métricas
- **Micrometer + Prometheus** - Métricas e observabilidade

### Segurança
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **BCrypt** - Criptografia de senhas
- **CORS** - Controle de acesso entre origens

### Qualidade e Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking para testes unitários
- **Spring Boot Test** - Testes de integração

### Utilitários
- **Jackson** - Serialização/deserialização JSON
- **Bean Validation** - Validação de dados
- **SendGrid** - Envio de emails (recuperação de senha)

## 📁 Arquitetura do Projeto

A aplicação segue o padrão MVC com arquitetura em camadas bem definidas:

```
src/main/java/com/itb/inf2fm/projetoback/
├── 📁 config/                  # Configurações do Spring
│   ├── AsyncConfig.java        # Configuração de processamento assíncrono
│   ├── CorsConfiguration.java  # Configuração CORS
│   ├── DatabaseConfig.java     # Configuração do banco de dados
│   ├── OpenApiConfig.java      # Configuração Swagger/OpenAPI
│   └── SecurityConfig.java     # Configuração de segurança
├── 📁 controller/              # Controllers REST (API endpoints)
│   ├── AdminController.java    # Endpoints administrativos
│   ├── ClienteController.java  # Gestão de clientes
│   ├── EspecialidadeController.java # Gestão de especialidades
│   ├── RegiaoController.java   # Gestão de regiões
│   ├── TecnicoController.java  # Gestão de técnicos
│   └── UsuarioController.java  # Gestão de usuários
├── 📁 dto/                     # Data Transfer Objects
│   ├── request/                # DTOs para requisições
│   └── response/               # DTOs para respostas
├── 📁 exception/               # Tratamento de exceções
│   ├── BusinessException.java  # Exceções de regra de negócio
│   ├── GlobalExceptionHandler.java # Handler global de exceções
│   └── ResourceNotFoundException.java # Exceções de recurso não encontrado
├── 📁 model/                   # Entidades JPA (Modelo de dados)
│   ├── Cliente.java           # Entidade Cliente
│   ├── Especialidade.java     # Entidade Especialidade
│   ├── PasswordResetToken.java # Token de recuperação de senha
│   ├── Regiao.java           # Entidade Região
│   ├── Tecnico.java          # Entidade Técnico
│   ├── TecnicoEspecialidade.java # Relacionamento Técnico-Especialidade
│   ├── TecnicoRegiao.java    # Relacionamento Técnico-Região
│   └── Usuario.java          # Entidade Usuário
├── 📁 repository/              # Repositories JPA (Acesso a dados)
│   ├── ClienteRepository.java
│   ├── EspecialidadeRepository.java
│   ├── PasswordResetTokenRepository.java
│   ├── RegiaoRepository.java
│   ├── TecnicoEspecialidadeRepository.java
│   ├── TecnicoRegiaoRepository.java
│   ├── TecnicoRepository.java
│   └── UsuarioRepository.java
├── 📁 service/                 # Lógica de negócio
│   ├── AsyncService.java      # Serviços assíncronos
│   ├── BatchService.java      # Processamento em lote
│   ├── CacheService.java      # Gerenciamento de cache
│   ├── ClienteService.java    # Lógica de negócio de clientes
│   ├── EspecialidadeService.java # Lógica de especialidades
│   ├── PasswordEncryptService.java # Criptografia de senhas
│   ├── RegiaoService.java     # Lógica de regiões
│   ├── TecnicoService.java    # Lógica de técnicos
│   └── UsuarioService.java    # Lógica de usuários
└── ProjetobackApplication.java # Classe principal
```

## 🔧 Configuração e Instalação

### Pré-requisitos
- **Java 21** ou superior ([Download JDK](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Download Maven](https://maven.apache.org/download.cgi))
- **Git** ([Download Git](https://git-scm.com/downloads))

### Clonando o Repositório
```bash
git clone https://github.com/RenanSilvaMartins/projetoback.git
cd projetoback
```

### Executando a Aplicação

#### Desenvolvimento (H2 em memória)
```bash
# Usando Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

#### Produção (SQL Server)
```bash
# Definir profile de produção
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### URLs Importantes

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **API Base** | `http://localhost:8082` | Endpoint principal da API |
| **Swagger UI** | `http://localhost:8082/swagger-ui.html` | Documentação interativa |
| **H2 Console** | `http://localhost:8082/h2-console` | Console do banco H2 (dev) |
| **Actuator Health** | `http://localhost:8082/actuator/health` | Status da aplicação |
| **Metrics** | `http://localhost:8082/actuator/metrics` | Métricas da aplicação |

## 📚 Documentação da API

### Swagger/OpenAPI
A documentação completa e interativa da API está disponível em:
- **Desenvolvimento**: `http://localhost:8082/swagger-ui.html`
- **Produção**: `https://api.empresa.com/swagger-ui.html`

### Documentação Adicional
- **[� Documentação da API](docs/API_DOCUMENTATION.md)** - Guia completo dos endpoints
- **[🔒 Documentação de Segurança](docs/SECURITY_DOCUMENTATION.md)** - Configurações de segurança
- **[📱 Exemplos Flutter](docs/examples/FLUTTER_EXAMPLES.dart)** - Integração com Flutter
- **[⚛️ Exemplos React](docs/examples/REACT_EXAMPLES.js)** - Integração com React/Vite

## 🚀 Integração com Frontend

### Flutter
```dart
// Configuração base para Flutter
class ApiConfig {
  static const String baseUrl = 'http://localhost:8082';
  static const String apiVersion = 'v1';
  
  static Map<String, String> get headers => {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };
}
```

### React/Vite
```javascript
// Configuração base para React
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8082',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
```

## 🔒 Segurança

### Autenticação JWT
- **Algoritmo**: HS256
- **Expiração**: 24 horas (configurável)
- **Header**: `Authorization: Bearer <token>`

### Endpoints Protegidos
- `/admin/**` - Apenas administradores
- `/usuario/**` - Usuários autenticados
- `/cliente/**` - Usuários autenticados
- `/tecnico/**` - Usuários autenticados

### Headers de Segurança
- **HSTS** - HTTP Strict Transport Security
- **X-Frame-Options** - Proteção contra clickjacking
- **X-Content-Type-Options** - Proteção contra MIME sniffing
- **Referrer-Policy** - Controle de referrer

## 🧪 Testes

### Executar Testes
```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=UsuarioServiceTest

# Testes com cobertura
./mvnw test jacoco:report
```

### Scripts de Teste
```bash
# Testar CRUD completo
./test-crud.sh

# Testar API geral
./test-api.sh
```

## 📊 Monitoramento e Observabilidade

### Health Checks
```bash
curl http://localhost:8082/actuator/health
```

### Métricas Prometheus
```bash
curl http://localhost:8082/actuator/prometheus
```

### Logs
- **Nível padrão**: INFO
- **Localização**: `logs/application.log`
- **Rotação**: Diária com retenção de 30 dias

## 🔄 Perfis de Ambiente

### Development (Padrão)
- Banco H2 em memória
- Log level: DEBUG
- CORS liberado
- Swagger habilitado

### Test
- Banco H2 em memória
- Dados de teste carregados
- Métricas desabilitadas

### Production
- SQL Server
- Log level: WARN
- CORS restrito
- Swagger desabilitado

## 🚀 Deploy

### Docker
```dockerfile
# Exemplo Dockerfile
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/projetoback-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Heroku
```bash
# Deploy para Heroku
heroku create nome-da-app
git push heroku main
```

## 🤝 Contribuição

1. **Fork** o projeto
2. Crie uma **feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### Padrões de Código
- Seguir convenções Java
- Documentar métodos públicos
- Escrever testes para novas funcionalidades
- Manter cobertura de testes > 80%

## 📝 Changelog

### v1.0.0 (Atual)
- ✅ Sistema de autenticação JWT
- ✅ CRUD completo para todas as entidades
- ✅ Documentação Swagger
- ✅ Testes unitários e de integração
- ✅ Monitoramento com Actuator
- ✅ Suporte a múltiplos ambientes

### Próximas Versões
- 🔄 Rate limiting
- 🔄 Cache distribuído (Redis)
- 🔄 Websockets para notificações
- 🔄 Upload de arquivos
- 🔄 Audit log

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Equipe

- **Desenvolvimento**: Equipe ITB INF2FM
- **Repositório**: [RenanSilvaMartins/projetoback](https://github.com/RenanSilvaMartins/projetoback)
- **Contato**: dev@itb.inf2fm.com

## 🆘 Suporte

Para suporte e dúvidas:
1. Consulte a [documentação](docs/)
2. Verifique as [issues existentes](https://github.com/RenanSilvaMartins/projetoback/issues)
3. Crie uma nova [issue](https://github.com/RenanSilvaMartins/projetoback/issues/new)

---

**Desenvolvido com ❤️ pela equipe ITB INF2FM**
- **[Flutter Examples](docs/examples/FLUTTER_EXAMPLES.dart)** - Exemplos para Flutter
- **[React Examples](docs/examples/REACT_EXAMPLES.js)** - Exemplos para React
- **[Password Reset Flutter](docs/examples/PASSWORD_RESET_FLUTTER.dart)** - Sistema de redefinição de senha Flutter
- **[Password Reset React](docs/examples/PASSWORD_RESET_REACT.js)** - Sistema de redefinição de senha React

## 🔐 Funcionalidades

### Autenticação
- Login com JWT
- Redefinição de senha com código de 6 dígitos
- Criptografia BCrypt

### Módulos
- **Usuários** - Gerenciamento de usuários do sistema
- **Clientes** - Cadastro e gestão de clientes
- **Técnicos** - Gerenciamento de técnicos
- **Especialidades** - Categorias de especialidades técnicas
- **Regiões** - Gestão de regiões de atendimento

### Endpoints Principais

#### Autenticação
- `POST /usuario/authenticate` - Login
- `POST /password-reset/generate` - Gerar código de redefinição
- `POST /password-reset/verify` - Verificar código
- `POST /password-reset/reset` - Redefinir senha

#### CRUD Completo
- `GET|POST|PUT|DELETE /usuario` - Usuários
- `GET|POST|PUT|DELETE /cliente` - Clientes
- `GET|POST|PUT|DELETE /tecnico` - Técnicos
- `GET|POST|PUT|DELETE /especialidade` - Especialidades
- `GET|POST|PUT|DELETE /regiao` - Regiões

## 🛡️ Segurança

- **JWT Authentication** para proteção de endpoints
- **BCrypt** para criptografia de senhas
- **CORS** configurado para desenvolvimento
- **Validação de CPF** para clientes e técnicos
- **Tokens de redefinição** com expiração de 15 minutos

## 🧪 Testes

Execute os testes com:
```bash
mvn test
```

## 📦 Build

Gerar JAR para produção:
```bash
mvn clean package
```

## 🔄 Inicialização

O sistema inclui dados padrão que podem ser inicializados via endpoints:
```bash
POST /especialidade/initialize
POST /regiao/initialize
```

## 📞 Suporte

Para dúvidas sobre integração com Frontend, consulte os arquivos de exemplo na pasta `docs/examples/`.

Λεονάρτο εστ ηέρε