# API REST - Documentação para Frontend

## Configuração Base

**Base URL:** `http://localhost:8082`

### Flutter Setup
```dart
class ApiService {
  static const String baseUrl = 'http://localhost:8082';
  
  static Map<String, String> get headers => {
    'Content-Type': 'application/json',
  };
}
```

### React Setup
```javascript
// axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8082',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
```

## Endpoints Principais

### 1. USUÁRIOS (`/usuario`)

#### Criar Usuário
```
POST /usuario
Body: {
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "123456",
  "nivelAcesso": "USER", // USER ou ADMIN
  "statusUsuario": "ATIVO" // ATIVO, INATIVO, TROCAR_SENHA
}
```

#### Autenticar (Login)
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