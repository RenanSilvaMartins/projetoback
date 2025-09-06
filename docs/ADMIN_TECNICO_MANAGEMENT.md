# Gerenciamento de Técnicos pelo Admin

Este documento descreve as novas funcionalidades adicionadas ao AdminController para gerenciar técnicos.

## Endpoints Disponíveis

### 1. Cadastrar Novo Técnico
**POST** `/admin/tecnicos`

Permite ao admin cadastrar um novo técnico no sistema.

#### Exemplo de Requisição:
```json
{
  "cpfCnpj": "12345678901",
  "dataNascimento": "1990-05-15",
  "telefone": "11999887766",
  "cep": "01234567",
  "numeroResidencia": "123",
  "complemento": "Apto 45",
  "descricao": "Técnico especializado em hardware e redes",
  "statusTecnico": "ATIVO",
  "usuario": {
    "nome": "João Silva",
    "email": "joao.silva@empresa.com",
    "senha": "senha123"
  }
}
```

#### Respostas:
- **201 Created**: Técnico cadastrado com sucesso
- **400 Bad Request**: Dados inválidos
- **500 Internal Server Error**: Erro interno

### 2. Listar Todos os Técnicos
**GET** `/admin/tecnicos`

Retorna uma lista com todos os técnicos cadastrados no sistema.

#### Exemplo de Resposta:
```json
[
  {
    "id": 1,
    "cpfCnpj": "12345678901",
    "dataNascimento": "1990-05-15",
    "telefone": "11999887766",
    "cep": "01234567",
    "numeroResidencia": "123",
    "complemento": "Apto 45",
    "descricao": "Técnico especializado em hardware e redes",
    "statusTecnico": "ATIVO",
    "usuario": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao.silva@empresa.com",
      "statusUsuario": "ATIVO",
      "nivelAcesso": "USER"
    }
  }
]
```

### 3. Remover Técnico
**DELETE** `/admin/tecnicos/{id}`

Remove completamente um técnico do sistema, incluindo seus relacionamentos.

#### Exemplo:
```
DELETE /admin/tecnicos/1
```

#### Respostas:
- **204 No Content**: Técnico removido com sucesso
- **404 Not Found**: Técnico não encontrado
- **400 Bad Request**: ID inválido
- **500 Internal Server Error**: Erro interno

### 4. Inativar Técnico
**PUT** `/admin/tecnicos/{id}/inativar`

Inativa um técnico sem removê-lo do sistema (soft delete).

#### Exemplo:
```
PUT /admin/tecnicos/1/inativar
```

#### Respostas:
- **200 OK**: Técnico inativado com sucesso
- **404 Not Found**: Técnico não encontrado
- **400 Bad Request**: ID inválido
- **500 Internal Server Error**: Erro interno

## Exemplos de Uso com cURL

### Cadastrar Técnico:
```bash
curl -X POST http://localhost:8082/admin/tecnicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "cpfCnpj": "12345678901",
    "dataNascimento": "1990-05-15",
    "telefone": "11999887766",
    "cep": "01234567",
    "numeroResidencia": "123",
    "complemento": "Apto 45",
    "descricao": "Técnico especializado em hardware",
    "usuario": {
      "nome": "João Silva",
      "email": "joao.silva@empresa.com",
      "senha": "senha123"
    }
  }'
```

### Listar Técnicos:
```bash
curl -X GET http://localhost:8082/admin/tecnicos \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Remover Técnico:
```bash
curl -X DELETE http://localhost:8082/admin/tecnicos/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Inativar Técnico:
```bash
curl -X PUT http://localhost:8082/admin/tecnicos/1/inativar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Exemplos para Frontend

### Flutter (usando http package):
```dart
import 'dart:convert';
import 'package:http/http.dart' as http;

class AdminTecnicoService {
  static const String baseUrl = 'http://localhost:8082/admin/tecnicos';
  
  // Cadastrar técnico
  static Future<Map<String, dynamic>> cadastrarTecnico(Map<String, dynamic> tecnicoData, String token) async {
    final response = await http.post(
      Uri.parse(baseUrl),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(tecnicoData),
    );
    
    if (response.statusCode == 201) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Erro ao cadastrar técnico: ${response.body}');
    }
  }
  
  // Listar técnicos
  static Future<List<dynamic>> listarTecnicos(String token) async {
    final response = await http.get(
      Uri.parse(baseUrl),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );
    
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Erro ao listar técnicos');
    }
  }
  
  // Remover técnico
  static Future<void> removerTecnico(int id, String token) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/$id'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );
    
    if (response.statusCode != 204) {
      throw Exception('Erro ao remover técnico');
    }
  }
}
```

### React/JavaScript (usando Axios):
```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8082/admin/tecnicos';

class AdminTecnicoService {
  // Cadastrar técnico
  static async cadastrarTecnico(tecnicoData, token) {
    try {
      const response = await axios.post(API_BASE_URL, tecnicoData, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(`Erro ao cadastrar técnico: ${error.response?.data || error.message}`);
    }
  }
  
  // Listar técnicos
  static async listarTecnicos(token) {
    try {
      const response = await axios.get(API_BASE_URL, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      throw new Error('Erro ao listar técnicos');
    }
  }
  
  // Remover técnico
  static async removerTecnico(id, token) {
    try {
      await axios.delete(`${API_BASE_URL}/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
    } catch (error) {
      throw new Error('Erro ao remover técnico');
    }
  }
  
  // Inativar técnico
  static async inativarTecnico(id, token) {
    try {
      const response = await axios.put(`${API_BASE_URL}/${id}/inativar`, {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      throw new Error('Erro ao inativar técnico');
    }
  }
}

export default AdminTecnicoService;
```

## Validações e Regras de Negócio

1. **Campos Obrigatórios**:
   - CPF/CNPJ
   - Data de nascimento
   - Telefone
   - CEP
   - Número da residência
   - Complemento
   - Descrição
   - Dados do usuário (nome, email, senha)

2. **Validações Automáticas**:
   - Senha é automaticamente criptografada com BCrypt
   - Status padrão é definido como "ATIVO"
   - Nível de acesso padrão é "USER"
   - Data de cadastro é definida automaticamente

3. **Relacionamentos**:
   - Ao remover um técnico, todos os relacionamentos com regiões e especialidades são removidos automaticamente
   - Inativar preserva os dados mas marca como inativo

## Segurança

- Todos os endpoints requerem autenticação JWT
- Apenas usuários com nível de acesso "ADMIN" podem acessar estes endpoints
- Senhas são automaticamente criptografadas antes de serem salvas no banco

## Swagger/OpenAPI

Todos os endpoints estão documentados no Swagger UI disponível em:
`http://localhost:8082/swagger-ui.html`

Procure pela seção "Admin" para ver a documentação interativa destes endpoints.