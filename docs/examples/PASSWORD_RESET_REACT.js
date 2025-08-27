// REACT - Sistema de Redefinição de Senha
import React, { useState } from 'react';
import axios from 'axios';

// Serviço de redefinição de senha
const passwordResetService = {
  // STEP 1: Gerar código
  async generateResetCode(email) {
    const response = await axios.post('/password-reset/generate', { email });
    return response.data;
  },

  // STEP 2: Verificar código
  async verifyResetCode(email, token) {
    const response = await axios.post('/password-reset/verify', { email, token });
    return response.data.success;
  },

  // STEP 3: Redefinir senha
  async resetPassword(email, token, newPassword) {
    const response = await axios.post('/password-reset/reset', {
      email,
      token,
      newPassword
    });
    return response.data.success;
  }
};

// COMPONENTE: Solicitar redefinição de senha
const ForgotPasswordForm = ({ onCodeSent }) => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!email) {
      setError('Digite seu email');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const result = await passwordResetService.generateResetCode(email);
      
      if (result.success) {
        onCodeSent(email, result.token); // Em produção, não passar o token
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao enviar código');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-form">
      <h2>Esqueci minha senha</h2>
      <p>Digite seu email para receber o código de redefinição</p>
      
      <form onSubmit={handleSubmit}>
        {error && <div className="error">{error}</div>}
        
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
            required
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar Código'}
        </button>
      </form>
    </div>
  );
};

// COMPONENTE: Verificar código
const VerifyCodeForm = ({ email, generatedToken, onCodeVerified }) => {
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (code.length !== 6) {
      setError('Digite o código de 6 dígitos');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const isValid = await passwordResetService.verifyResetCode(email, code);
      
      if (isValid) {
        onCodeVerified(code);
      } else {
        setError('Código inválido ou expirado');
      }
    } catch (err) {
      setError('Erro ao verificar código');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="verify-code-form">
      <h2>Verificar Código</h2>
      <p>Digite o código de 6 dígitos enviado para {email}</p>
      
      {/* Em produção, remover esta linha */}
      <p style={{color: 'red', fontSize: '12px'}}>
        Código gerado: {generatedToken}
      </p>
      
      <form onSubmit={handleSubmit}>
        {error && <div className="error">{error}</div>}
        
        <div className="form-group">
          <label>Código (6 dígitos):</label>
          <input
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
            placeholder="123456"
            maxLength={6}
            required
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Verificando...' : 'Verificar Código'}
        </button>
      </form>
    </div>
  );
};

// COMPONENTE: Nova senha
const NewPasswordForm = ({ email, token, onPasswordReset }) => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (password.length < 6) {
      setError('Senha deve ter pelo menos 6 caracteres');
      return;
    }
    
    if (password !== confirmPassword) {
      setError('Senhas não coincidem');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const success = await passwordResetService.resetPassword(email, token, password);
      
      if (success) {
        onPasswordReset();
      } else {
        setError('Erro ao redefinir senha. Tente novamente.');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao redefinir senha');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="new-password-form">
      <h2>Nova Senha</h2>
      <p>Digite sua nova senha</p>
      
      <form onSubmit={handleSubmit}>
        {error && <div className="error">{error}</div>}
        
        <div className="form-group">
          <label>Nova Senha:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Mínimo 6 caracteres"
            required
          />
        </div>
        
        <div className="form-group">
          <label>Confirmar Senha:</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Digite a senha novamente"
            required
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Redefinindo...' : 'Redefinir Senha'}
        </button>
      </form>
    </div>
  );
};

// COMPONENTE PRINCIPAL: Fluxo completo de redefinição
const PasswordResetFlow = ({ onComplete }) => {
  const [step, setStep] = useState(1); // 1: email, 2: código, 3: nova senha
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [generatedToken, setGeneratedToken] = useState(''); // Em produção, remover

  const handleCodeSent = (userEmail, genToken) => {
    setEmail(userEmail);
    setGeneratedToken(genToken); // Em produção, remover
    setStep(2);
  };

  const handleCodeVerified = (verifiedToken) => {
    setToken(verifiedToken);
    setStep(3);
  };

  const handlePasswordReset = () => {
    alert('Senha redefinida com sucesso!');
    onComplete?.();
  };

  return (
    <div className="password-reset-flow">
      {step === 1 && (
        <ForgotPasswordForm onCodeSent={handleCodeSent} />
      )}
      
      {step === 2 && (
        <VerifyCodeForm 
          email={email}
          generatedToken={generatedToken}
          onCodeVerified={handleCodeVerified}
        />
      )}
      
      {step === 3 && (
        <NewPasswordForm 
          email={email}
          token={token}
          onPasswordReset={handlePasswordReset}
        />
      )}
      
      {/* Indicador de progresso */}
      <div className="progress-indicator">
        <div className={`step ${step >= 1 ? 'active' : ''}`}>1. Email</div>
        <div className={`step ${step >= 2 ? 'active' : ''}`}>2. Código</div>
        <div className={`step ${step >= 3 ? 'active' : ''}`}>3. Nova Senha</div>
      </div>
    </div>
  );
};

// Hook personalizado para redefinição de senha
const usePasswordReset = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const generateCode = async (email) => {
    setLoading(true);
    setError('');
    
    try {
      const result = await passwordResetService.generateResetCode(email);
      return result;
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao gerar código');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const verifyCode = async (email, token) => {
    setLoading(true);
    setError('');
    
    try {
      return await passwordResetService.verifyResetCode(email, token);
    } catch (err) {
      setError('Erro ao verificar código');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const resetPassword = async (email, token, newPassword) => {
    setLoading(true);
    setError('');
    
    try {
      return await passwordResetService.resetPassword(email, token, newPassword);
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao redefinir senha');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    generateCode,
    verifyCode,
    resetPassword,
    loading,
    error
  };
};

// CSS básico (adicionar ao seu arquivo CSS)
const styles = `
.password-reset-flow {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.error {
  background-color: #fee;
  color: #c33;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.progress-indicator {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.step {
  padding: 5px 10px;
  border-radius: 15px;
  background-color: #f8f9fa;
  color: #6c757d;
  font-size: 12px;
}

.step.active {
  background-color: #007bff;
  color: white;
}
`;

export default PasswordResetFlow;
export { usePasswordReset, passwordResetService };