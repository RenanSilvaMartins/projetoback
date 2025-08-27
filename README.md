# Sistema de Gerenciamento - Backend

API REST desenvolvida com Spring Boot para gerenciamento de usuários, clientes, técnicos, especialidades e regiões.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Security**
- **Spring Data JPA**
- **SQL Server**
- **JWT Authentication**
- **BCrypt Password Encryption**

## 📁 Estrutura do Projeto

```
src/main/java/com/itb/inf2fm/projetoback/
├── config/                 # Configurações do Spring
├── controller/             # Controllers REST
├── dto/                    # Data Transfer Objects
├── exceptions/             # Tratamento de exceções
├── model/                  # Entidades JPA
├── repository/             # Repositories JPA
├── security/               # Configurações de segurança
├── service/                # Lógica de negócio
├── util/                   # Classes utilitárias
└── ProjetobackApplication.java
```

## 🔧 Configuração

### Pré-requisitos
- Java 21+
- Maven 3.6+
- SQL Server

### Executar o projeto
```bash
mvn spring-boot:run
```

### Porta padrão
```
http://localhost:8083
```

## 📚 Documentação

- **[API Documentation](docs/API_DOCUMENTATION.md)** - Documentação completa da API
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