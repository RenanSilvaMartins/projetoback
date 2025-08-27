# 📖 API REST - Documentação Completa

[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)](http://localhost:8082/swagger-ui.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white)](https://www.postman.com/)

Esta documentação fornece um guia completo para integração com a API REST do Sistema de Gerenciamento, incluindo exemplos práticos para **Flutter** e **React/Vite**.

## 🌐 Configuração Base

### URLs do Ambiente

| Ambiente | Base URL | Swagger UI | Status |
|----------|----------|------------|--------|
| **Desenvolvimento** | `http://localhost:8082` | [Swagger Dev](http://localhost:8082/swagger-ui.html) | 🟢 Ativo |
| **Produção** | `https://api.empresa.com` | [Swagger Prod](https://api.empresa.com/swagger-ui.html) | 🟢 Ativo |

### Configuração do Cliente HTTP

#### Flutter Setup
```dart
// lib/services/api_service.dart
import 'package:dio/dio.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8082';
  static const Duration timeout = Duration(seconds: 30);
  
  static final Dio _dio = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: timeout,
      receiveTimeout: timeout,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ),
  );

  // Interceptor para token JWT
  static void addAuthInterceptor() {
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await getStoredToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          handler.next(options);
        },
        onError: (error, handler) {
          if (error.response?.statusCode == 401) {
            // Token expirado - redirecionar para login
            clearStoredToken();
          }
          handler.next(error);
        },
      ),
    );
  }

  static Dio get dio => _dio;
}
```

#### React/Vite Setup
```javascript
// src/services/api.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8082';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Interceptor para token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para tratamento de respostas
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado - limpar storage e redirecionar
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

## 🔐 Autenticação e Autorização

### Sistema JWT

A API utiliza **JSON Web Tokens (JWT)** para autenticação stateless com as seguintes características:

- **Algoritmo**: HS256
- **Expiração**: 24 horas (86400 segundos)
- **Header**: `Authorization: Bearer <token>`
- **Refresh**: Automático (implementar se necessário)

### Estrutura do Token JWT

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "usuario@email.com",
    "userId": 1,
    "nome": "João Silva",
    "nivelAcesso": "USER",
    "iat": 1642752000,
    "exp": 1642838400
  }
}
```

### Níveis de Acesso

| Nível | Descrição | Permissões |
|-------|-----------|------------|
| **USER** | Usuário padrão | CRUD próprios dados |
| **ADMIN** | Administrador | CRUD todos os dados + gestão de usuários |

## 📋 Endpoints da API

### 🔑 Autenticação (`/auth`, `/usuario`)

#### Login de Usuário
```http
POST /usuario/login
Content-Type: application/json

{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": 1,
    "nome": "João Silva",
    "email": "usuario@email.com",
    "nivelAcesso": "USER",
    "expiresIn": 86400
  },
  "timestamp": "2024-01-20T10:30:00"
}
```

#### Exemplo Flutter - Login
```dart
// lib/services/auth_service.dart
class AuthService {
  static Future<AuthResponse?> login(String email, String senha) async {
    try {
      final response = await ApiService.dio.post(
        '/usuario/login',
        data: {
          'email': email,
          'senha': senha,
        },
      );

      if (response.data['success']) {
        final authData = AuthResponse.fromJson(response.data['data']);
        await _storeToken(authData.token);
        return authData;
      }
      return null;
    } on DioException catch (e) {
      throw AuthException(e.response?.data['message'] ?? 'Erro no login');
    }
  }

  static Future<void> _storeToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('auth_token', token);
  }
}

class AuthResponse {
  final String token;
  final String type;
  final int userId;
  final String nome;
  final String email;
  final String nivelAcesso;
  final int expiresIn;

  AuthResponse({
    required this.token,
    required this.type,
    required this.userId,
    required this.nome,
    required this.email,
    required this.nivelAcesso,
    required this.expiresIn,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'],
      type: json['type'],
      userId: json['userId'],
      nome: json['nome'],
      email: json['email'],
      nivelAcesso: json['nivelAcesso'],
      expiresIn: json['expiresIn'],
    );
  }
}
```

#### Exemplo React - Login
```javascript
// src/services/authService.js
import api from './api';

export class AuthService {
  static async login(email, senha) {
    try {
      const response = await api.post('/usuario/login', {
        email,
        senha
      });

      if (response.data.success) {
        const authData = response.data.data;
        localStorage.setItem('authToken', authData.token);
        localStorage.setItem('userData', JSON.stringify({
          userId: authData.userId,
          nome: authData.nome,
          email: authData.email,
          nivelAcesso: authData.nivelAcesso
        }));
        return authData;
      }
      throw new Error(response.data.message || 'Erro no login');
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Erro de conexão'
      );
    }
  }

  static async logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
  }

  static getCurrentUser() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
  }

  static isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }
}
```

#### Criar Novo Usuário
```http
POST /usuario
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "123456",
  "nivelAcesso": "USER",
  "statusUsuario": "ATIVO"
}
```

**Estados do Usuário:**
- `ATIVO` - Usuário ativo no sistema
- `INATIVO` - Usuário desativado
- `TROCAR_SENHA` - Usuário deve trocar senha no próximo login

### 👥 Usuários (`/usuario`)

#### Listar Todos os Usuários
```http
GET /usuario
Authorization: Bearer <token>
```

**Com Paginação:**
```http
GET /usuario?page=0&size=20&sort=nome,asc
Authorization: Bearer <token>
```

#### Buscar Usuário por ID
```http
GET /usuario/{id}
Authorization: Bearer <token>
```

#### Atualizar Usuário
```http
PUT /usuario/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "email": "joao.novo@email.com",
  "nivelAcesso": "ADMIN"
}
```

#### Exemplo Flutter - Gestão de Usuários
```dart
// lib/services/user_service.dart
class UserService {
  static Future<List<Usuario>> getUsuarios({
    int page = 0,
    int size = 20,
    String? search,
  }) async {
    try {
      final queryParams = {
        'page': page.toString(),
        'size': size.toString(),
      };
      
      if (search != null && search.isNotEmpty) {
        queryParams['search'] = search;
      }

      final response = await ApiService.dio.get(
        '/usuario',
        queryParameters: queryParams,
      );

      if (response.data['success']) {
        final List<dynamic> usersData = response.data['data']['content'];
        return usersData.map((json) => Usuario.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      throw Exception('Erro ao carregar usuários: $e');
    }
  }

  static Future<Usuario?> getUsuarioById(int id) async {
    try {
      final response = await ApiService.dio.get('/usuario/$id');
      
      if (response.data['success']) {
        return Usuario.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      throw Exception('Erro ao carregar usuário: $e');
    }
  }

  static Future<Usuario> updateUsuario(int id, Usuario usuario) async {
    try {
      final response = await ApiService.dio.put(
        '/usuario/$id',
        data: usuario.toJson(),
      );

      if (response.data['success']) {
        return Usuario.fromJson(response.data['data']);
      }
      throw Exception(response.data['message']);
    } catch (e) {
      throw Exception('Erro ao atualizar usuário: $e');
    }
  }
}

class Usuario {
  final int? id;
  final String nome;
  final String email;
  final String? nivelAcesso;
  final String? statusUsuario;
  final DateTime? dataCadastro;

  Usuario({
    this.id,
    required this.nome,
    required this.email,
    this.nivelAcesso,
    this.statusUsuario,
    this.dataCadastro,
  });

  factory Usuario.fromJson(Map<String, dynamic> json) {
    return Usuario(
      id: json['id'],
      nome: json['nome'],
      email: json['email'],
      nivelAcesso: json['nivelAcesso'],
      statusUsuario: json['statusUsuario'],
      dataCadastro: json['dataCadastro'] != null 
          ? DateTime.parse(json['dataCadastro']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'nome': nome,
      'email': email,
      'nivelAcesso': nivelAcesso,
      'statusUsuario': statusUsuario,
    };
  }
}
```
POST /usuario/authenticate
Body: {
  "email": "joao@email.com",
  "senha": "123456"
}
Response: Usuario (sem senha) ou 401
```

#### Listar Usuários
```
GET /usuario
Response: List<Usuario>
```

### 2. CLIENTES (`/cliente`)

#### Criar Cliente
```
POST /cliente
Body: {
  "cpf": "11144477735", // CPF válido obrigatório
  "dataNascimento": "1990-01-01",
  "usuario": {
    "nome": "Maria Silva",
    "email": "maria@email.com",
    "senha": "123456"
  },
  "statusCliente": "ATIVO"
}
```

#### Listar Clientes
```
GET /cliente
Response: List<Cliente>
```

### 3. ESPECIALIDADES (`/especialidade`)

#### Listar Ativas (para dropdowns)
```
GET /especialidade/ativas
Response: List<Especialidade>
```

#### Inicializar Dados Padrão
```
POST /especialidade/initialize
Response: "Especialidades padrão inicializadas com sucesso"
```

### 4. REGIÕES (`/regiao`)

#### Listar Ativas (para dropdowns)
```
GET /regiao/ativas
Response: List<Regiao>
```

#### Buscar por Cidade
```
GET /regiao/cidade/{cidade}
Response: List<Regiao>
```

### 5. TÉCNICOS (`/tecnico`)

#### Criar Técnico
```
POST /tecnico
Body: {
  "cpfCnpj": "12345678901",
  "dataNascimento": "1990-01-01",
  "telefone": "11999999999",
  "cep": "01234567",
  "numeroResidencia": "123",
  "complemento": "Apto 45",
  "descricao": "Técnico especializado",
  "statusTecnico": "ATIVO",
  "usuario": {
    "nome": "Pedro Técnico",
    "email": "pedro@email.com",
    "senha": "123456"
  }
}
```

## Códigos de Status HTTP

- **200**: Sucesso
- **201**: Criado com sucesso
- **400**: Dados inválidos
- **401**: Não autorizado
- **404**: Não encontrado
- **500**: Erro interno do servidor

## Validações Importantes

### CPF
- Deve ser um CPF válido (11 dígitos)
- Exemplo válido: "11144477735"

### Email
- Deve ser um email válido
- Único no sistema

### Senha
- Mínimo 6 caracteres
- É criptografada automaticamente

### Status
- **Usuario**: ATIVO, INATIVO, TROCAR_SENHA
- **Cliente**: ATIVO, INATIVO
- **Tecnico**: ATIVO, INATIVO
- **Especialidade**: ATIVO, INATIVO
- **Regiao**: ATIVO, INATIVO

## Exemplos de Uso

### Flutter - Criar Cliente
```dart
Future<Map<String, dynamic>> criarCliente(Map<String, dynamic> clienteData) async {
  final response = await http.post(
    Uri.parse('${ApiService.baseUrl}/cliente'),
    headers: ApiService.headers,
    body: jsonEncode(clienteData),
  );
  
  if (response.statusCode == 201) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Erro ao criar cliente');
  }
}
```

### React - Login
```javascript
const login = async (email, senha) => {
  try {
    const response = await api.post('/usuario/authenticate', {
      email,
      senha
    });
    
    if (response.status === 200) {
      return response.data;
    }
  } catch (error) {
    if (error.response?.status === 401) {
      throw new Error('Credenciais inválidas');
    }
    throw new Error('Erro no servidor');
  }
};
```

## Inicialização do Sistema

Para popular dados iniciais:
```
POST /especialidade/initialize
POST /regiao/initialize
```

Isso criará especialidades e regiões padrão para uso no sistema.