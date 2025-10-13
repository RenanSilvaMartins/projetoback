# Exemplos de Agendamento com Restrição por Especialidade

## Funcionalidade Implementada

O sistema agora restringe a seleção de técnicos apenas àqueles que possuem a especialidade necessária para o serviço escolhido.

## Endpoints Disponíveis

### 1. Buscar Técnicos por Serviço
```http
GET /agendamento/tecnicos-por-servico/{servicoId}
```

**Exemplo de Resposta:**
```json
[
  {
    "id": 1,
    "cpfCnpj": "12345678901",
    "especialidade": "Eletricista",
    "statusTecnico": "ATIVO",
    "usuario": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com"
    }
  }
]
```

### 2. Criar Agendamento com Validação
```http
POST /agendamento
Content-Type: application/json

{
  "usuarioId": 1,
  "tecnicoId": 2,
  "servicoId": 1,
  "clienteId": 1,
  "dataAgendamento": "2024-01-15",
  "horaAgendamento": "14:00",
  "descricao": "Instalação elétrica",
  "urgencia": "NORMAL",
  "situacao": "AGENDADO",
  "preco": 150.00
}
```

## Fluxo de Uso Recomendado

### 1. Primeiro, busque os serviços disponíveis:
```http
GET /agendamento/servicos-disponiveis
```

### 2. Após selecionar um serviço, busque os técnicos qualificados:
```http
GET /agendamento/tecnicos-por-servico/1
```

### 3. Crie o agendamento com técnico validado:
```http
POST /agendamento
```

## Validações Implementadas

- ✅ Técnico deve possuir a especialidade necessária para o serviço
- ✅ Validação aplicada tanto na criação quanto na atualização
- ✅ Mensagem de erro clara quando técnico não possui especialidade

## Exemplo de Erro

Se tentar agendar um técnico sem a especialidade necessária:

```json
{
  "error": "Técnico não possui a especialidade necessária para este serviço"
}
```

## Integração Frontend

### Flutter
```dart
// 1. Buscar técnicos por serviço
Future<List<Tecnico>> getTecnicosPorServico(int servicoId) async {
  final response = await http.get(
    Uri.parse('$baseUrl/agendamento/tecnicos-por-servico/$servicoId'),
    headers: headers,
  );
  
  if (response.statusCode == 200) {
    List<dynamic> data = json.decode(response.body);
    return data.map((json) => Tecnico.fromJson(json)).toList();
  }
  throw Exception('Erro ao buscar técnicos');
}

// 2. Criar agendamento
Future<Agendamento> criarAgendamento(AgendamentoRequest request) async {
  final response = await http.post(
    Uri.parse('$baseUrl/agendamento'),
    headers: headers,
    body: json.encode(request.toJson()),
  );
  
  if (response.statusCode == 200) {
    return Agendamento.fromJson(json.decode(response.body));
  }
  throw Exception('Erro ao criar agendamento');
}
```

### React
```javascript
// 1. Buscar técnicos por serviço
const getTecnicosPorServico = async (servicoId) => {
  try {
    const response = await api.get(`/agendamento/tecnicos-por-servico/${servicoId}`);
    return response.data;
  } catch (error) {
    throw new Error('Erro ao buscar técnicos');
  }
};

// 2. Criar agendamento
const criarAgendamento = async (agendamentoData) => {
  try {
    const response = await api.post('/agendamento', agendamentoData);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Erro ao criar agendamento');
  }
};
```