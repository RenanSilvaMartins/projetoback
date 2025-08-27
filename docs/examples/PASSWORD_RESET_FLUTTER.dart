// FLUTTER - Sistema de Redefinição de Senha
import 'dart:convert';
import 'package:http/http.dart' as http;

class PasswordResetService {
  static const String baseUrl = 'http://localhost:8082';
  
  static Map<String, String> get headers => {
    'Content-Type': 'application/json',
  };
  
  // STEP 1: Gerar código de redefinição
  static Future<Map<String, dynamic>> generateResetCode(String email) async {
    final response = await http.post(
      Uri.parse('$baseUrl/password-reset/generate'),
      headers: headers,
      body: jsonEncode({'email': email}),
    );
    
    final data = jsonDecode(response.body);
    
    if (response.statusCode == 200 && data['success']) {
      return data;
    } else {
      throw Exception(data['message'] ?? 'Erro ao gerar código');
    }
  }
  
  // STEP 2: Verificar código
  static Future<bool> verifyResetCode(String email, String token) async {
    final response = await http.post(
      Uri.parse('$baseUrl/password-reset/verify'),
      headers: headers,
      body: jsonEncode({
        'email': email,
        'token': token,
      }),
    );
    
    final data = jsonDecode(response.body);
    return response.statusCode == 200 && data['success'];
  }
  
  // STEP 3: Redefinir senha
  static Future<bool> resetPassword(String email, String token, String newPassword) async {
    final response = await http.post(
      Uri.parse('$baseUrl/password-reset/reset'),
      headers: headers,
      body: jsonEncode({
        'email': email,
        'token': token,
        'newPassword': newPassword,
      }),
    );
    
    final data = jsonDecode(response.body);
    return response.statusCode == 200 && data['success'];
  }
}

// WIDGET: Tela de solicitação de redefinição
class ForgotPasswordPage extends StatefulWidget {
  @override
  _ForgotPasswordPageState createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final _emailController = TextEditingController();
  bool _loading = false;
  
  Future<void> _sendResetCode() async {
    if (_emailController.text.isEmpty) {
      _showError('Digite seu email');
      return;
    }
    
    setState(() => _loading = true);
    
    try {
      final result = await PasswordResetService.generateResetCode(_emailController.text);
      
      // Navegar para tela de verificação
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => VerifyCodePage(
            email: _emailController.text,
            generatedToken: result['token'], // Em produção, não mostrar o token
          ),
        ),
      );
    } catch (e) {
      _showError(e.toString());
    } finally {
      setState(() => _loading = false);
    }
  }
  
  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Esqueci minha senha')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              'Digite seu email para receber o código de redefinição',
              style: TextStyle(fontSize: 16),
            ),
            SizedBox(height: 20),
            TextField(
              controller: _emailController,
              decoration: InputDecoration(
                labelText: 'Email',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.emailAddress,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loading ? null : _sendResetCode,
              child: _loading 
                ? CircularProgressIndicator() 
                : Text('Enviar Código'),
            ),
          ],
        ),
      ),
    );
  }
}

// WIDGET: Tela de verificação do código
class VerifyCodePage extends StatefulWidget {
  final String email;
  final String generatedToken; // Em produção, remover isso
  
  VerifyCodePage({required this.email, required this.generatedToken});
  
  @override
  _VerifyCodePageState createState() => _VerifyCodePageState();
}

class _VerifyCodePageState extends State<VerifyCodePage> {
  final _codeController = TextEditingController();
  bool _loading = false;
  
  Future<void> _verifyCode() async {
    if (_codeController.text.length != 6) {
      _showError('Digite o código de 6 dígitos');
      return;
    }
    
    setState(() => _loading = true);
    
    try {
      final isValid = await PasswordResetService.verifyResetCode(
        widget.email,
        _codeController.text,
      );
      
      if (isValid) {
        // Navegar para tela de nova senha
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => NewPasswordPage(
              email: widget.email,
              token: _codeController.text,
            ),
          ),
        );
      } else {
        _showError('Código inválido ou expirado');
      }
    } catch (e) {
      _showError(e.toString());
    } finally {
      setState(() => _loading = false);
    }
  }
  
  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Verificar Código')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              'Digite o código de 6 dígitos enviado para ${widget.email}',
              style: TextStyle(fontSize: 16),
            ),
            SizedBox(height: 10),
            // Em produção, remover esta linha
            Text('Código gerado: ${widget.generatedToken}', 
                 style: TextStyle(color: Colors.red, fontSize: 12)),
            SizedBox(height: 20),
            TextField(
              controller: _codeController,
              decoration: InputDecoration(
                labelText: 'Código (6 dígitos)',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
              maxLength: 6,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loading ? null : _verifyCode,
              child: _loading 
                ? CircularProgressIndicator() 
                : Text('Verificar Código'),
            ),
          ],
        ),
      ),
    );
  }
}

// WIDGET: Tela de nova senha
class NewPasswordPage extends StatefulWidget {
  final String email;
  final String token;
  
  NewPasswordPage({required this.email, required this.token});
  
  @override
  _NewPasswordPageState createState() => _NewPasswordPageState();
}

class _NewPasswordPageState extends State<NewPasswordPage> {
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  bool _loading = false;
  
  Future<void> _resetPassword() async {
    if (_passwordController.text.length < 6) {
      _showError('Senha deve ter pelo menos 6 caracteres');
      return;
    }
    
    if (_passwordController.text != _confirmPasswordController.text) {
      _showError('Senhas não coincidem');
      return;
    }
    
    setState(() => _loading = true);
    
    try {
      final success = await PasswordResetService.resetPassword(
        widget.email,
        widget.token,
        _passwordController.text,
      );
      
      if (success) {
        _showSuccess('Senha redefinida com sucesso!');
        // Voltar para tela de login
        Navigator.popUntil(context, (route) => route.isFirst);
      } else {
        _showError('Erro ao redefinir senha. Tente novamente.');
      }
    } catch (e) {
      _showError(e.toString());
    } finally {
      setState(() => _loading = false);
    }
  }
  
  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }
  
  void _showSuccess(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.green),
    );
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Nova Senha')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              'Digite sua nova senha',
              style: TextStyle(fontSize: 16),
            ),
            SizedBox(height: 20),
            TextField(
              controller: _passwordController,
              decoration: InputDecoration(
                labelText: 'Nova Senha',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            SizedBox(height: 16),
            TextField(
              controller: _confirmPasswordController,
              decoration: InputDecoration(
                labelText: 'Confirmar Senha',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loading ? null : _resetPassword,
              child: _loading 
                ? CircularProgressIndicator() 
                : Text('Redefinir Senha'),
            ),
          ],
        ),
      ),
    );
  }
}