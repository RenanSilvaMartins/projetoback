// FLUTTER - Exemplos de integração com a API
// Adicione estas dependências no pubspec.yaml:
// dependencies:
//   http: ^1.1.0

import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = 'http://localhost:8082';
  
  static Map<String, String> get headers => {
    'Content-Type': 'application/json',
  };
}

// MODELOS
class Usuario {
  final int? id;
  final String nome;
  final String email;
  final String? senha;
  final String? nivelAcesso;
  final String? statusUsuario;
  
  Usuario({
    this.id,
    required this.nome,
    required this.email,
    this.senha,
    this.nivelAcesso,
    this.statusUsuario,
  });
  
  Map<String, dynamic> toJson() => {
    'nome': nome,
    'email': email,
    'senha': senha,
    'nivelAcesso': nivelAcesso,
    'statusUsuario': statusUsuario,
  };
  
  factory Usuario.fromJson(Map<String, dynamic> json) => Usuario(
    id: json['id'],
    nome: json['nome'],
    email: json['email'],
    nivelAcesso: json['nivelAcesso'],
    statusUsuario: json['statusUsuario'],
  );
}

class Cliente {
  final int? id;
  final String cpf;
  final String dataNascimento;
  final Usuario usuario;
  final String statusCliente;
  
  Cliente({
    this.id,
    required this.cpf,
    required this.dataNascimento,
    required this.usuario,
    required this.statusCliente,
  });
  
  Map<String, dynamic> toJson() => {
    'cpf': cpf,
    'dataNascimento': dataNascimento,
    'usuario': usuario.toJson(),
    'statusCliente': statusCliente,
  };
}

// SERVIÇOS
class UsuarioService {
  // LOGIN
  static Future<Usuario?> login(String email, String senha) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiService.baseUrl}/usuario/authenticate'),
        headers: ApiService.headers,
        body: jsonEncode({
          'email': email,
          'senha': senha,
        }),
      );
      
      if (response.statusCode == 200) {
        return Usuario.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        throw Exception('Credenciais inválidas');
      } else {
        throw Exception('Erro no servidor');
      }
    } catch (e) {
      throw Exception('Erro de conexão: $e');
    }
  }
  
  // CRIAR USUÁRIO
  static Future<Usuario> criarUsuario(Usuario usuario) async {
    final response = await http.post(
      Uri.parse('${ApiService.baseUrl}/usuario'),
      headers: ApiService.headers,
      body: jsonEncode(usuario.toJson()),
    );
    
    if (response.statusCode == 201) {
      return Usuario.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Erro ao criar usuário');
    }
  }
  
  // LISTAR USUÁRIOS
  static Future<List<Usuario>> listarUsuarios() async {
    final response = await http.get(
      Uri.parse('${ApiService.baseUrl}/usuario'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Usuario.fromJson(json)).toList();
    } else {
      throw Exception('Erro ao carregar usuários');
    }
  }
}

class ClienteService {
  // CRIAR CLIENTE
  static Future<Map<String, dynamic>> criarCliente(Cliente cliente) async {
    final response = await http.post(
      Uri.parse('${ApiService.baseUrl}/cliente'),
      headers: ApiService.headers,
      body: jsonEncode(cliente.toJson()),
    );
    
    final data = jsonDecode(response.body);
    
    if (response.statusCode == 201 && data['valid'] == true) {
      return data;
    } else {
      throw Exception(data['mensagemErro'] ?? 'Erro ao criar cliente');
    }
  }
  
  // LISTAR CLIENTES
  static Future<List<Map<String, dynamic>>> listarClientes() async {
    final response = await http.get(
      Uri.parse('${ApiService.baseUrl}/cliente'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(jsonDecode(response.body));
    } else {
      throw Exception('Erro ao carregar clientes');
    }
  }
}

class EspecialidadeService {
  // LISTAR ESPECIALIDADES ATIVAS (para dropdowns)
  static Future<List<Map<String, dynamic>>> listarAtivas() async {
    final response = await http.get(
      Uri.parse('${ApiService.baseUrl}/especialidade/ativas'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(jsonDecode(response.body));
    } else {
      throw Exception('Erro ao carregar especialidades');
    }
  }
  
  // INICIALIZAR DADOS PADRÃO
  static Future<String> inicializar() async {
    final response = await http.post(
      Uri.parse('${ApiService.baseUrl}/especialidade/initialize'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw Exception('Erro ao inicializar especialidades');
    }
  }
}

class RegiaoService {
  // LISTAR REGIÕES ATIVAS (para dropdowns)
  static Future<List<Map<String, dynamic>>> listarAtivas() async {
    final response = await http.get(
      Uri.parse('${ApiService.baseUrl}/regiao/ativas'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(jsonDecode(response.body));
    } else {
      throw Exception('Erro ao carregar regiões');
    }
  }
  
  // BUSCAR POR CIDADE
  static Future<List<Map<String, dynamic>>> buscarPorCidade(String cidade) async {
    final response = await http.get(
      Uri.parse('${ApiService.baseUrl}/regiao/cidade/$cidade'),
      headers: ApiService.headers,
    );
    
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(jsonDecode(response.body));
    } else {
      throw Exception('Erro ao buscar regiões da cidade');
    }
  }
}

// EXEMPLO DE USO EM WIDGET
/*
class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _emailController = TextEditingController();
  final _senhaController = TextEditingController();
  bool _loading = false;
  
  Future<void> _login() async {
    setState(() => _loading = true);
    
    try {
      final usuario = await UsuarioService.login(
        _emailController.text,
        _senhaController.text,
      );
      
      if (usuario != null) {
        // Navegar para tela principal
        Navigator.pushReplacementNamed(context, '/home');
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    } finally {
      setState(() => _loading = false);
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _emailController,
              decoration: InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: _senhaController,
              decoration: InputDecoration(labelText: 'Senha'),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loading ? null : _login,
              child: _loading 
                ? CircularProgressIndicator() 
                : Text('Login'),
            ),
          ],
        ),
      ),
    );
  }
}
*/