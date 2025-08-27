// REACT + VITE - Exemplos de integração com a API
// Instale as dependências: npm install axios

// api.js - Configuração do Axios
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8082',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirecionar para login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

// services/usuarioService.js
import api from '../api';

export const usuarioService = {
  // LOGIN
  async login(email, senha) {
    try {
      const response = await api.post('/usuario/authenticate', {
        email,
        senha
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 401) {
        throw new Error('Credenciais inválidas');
      }
      throw new Error('Erro no servidor');
    }
  },

  // CRIAR USUÁRIO
  async criar(usuario) {
    const response = await api.post('/usuario', usuario);
    return response.data;
  },

  // LISTAR USUÁRIOS
  async listar() {
    const response = await api.get('/usuario');
    return response.data;
  },

  // BUSCAR POR ID
  async buscarPorId(id) {
    const response = await api.get(`/usuario/${id}`);
    return response.data;
  },

  // ATUALIZAR
  async atualizar(id, usuario) {
    const response = await api.put(`/usuario/${id}`, usuario);
    return response.data;
  },

  // DELETAR
  async deletar(id) {
    await api.delete(`/usuario/${id}`);
  }
};

// services/clienteService.js
import api from '../api';

export const clienteService = {
  // CRIAR CLIENTE
  async criar(cliente) {
    try {
      const response = await api.post('/cliente', cliente);
      
      if (response.data.valid) {
        return response.data;
      } else {
        throw new Error(response.data.mensagemErro || 'Erro ao criar cliente');
      }
    } catch (error) {
      if (error.response?.data?.mensagemErro) {
        throw new Error(error.response.data.mensagemErro);
      }
      throw error;
    }
  },

  // LISTAR CLIENTES
  async listar() {
    const response = await api.get('/cliente');
    return response.data;
  },

  // BUSCAR POR ID
  async buscarPorId(id) {
    const response = await api.get(`/cliente/cliente/${id}`);
    return response.data;
  },

  // ATUALIZAR
  async atualizar(id, cliente) {
    const response = await api.put(`/cliente/${id}`, cliente);
    return response.data;
  },

  // DELETAR
  async deletar(id) {
    await api.delete(`/cliente/${id}`);
  }
};

// services/especialidadeService.js
import api from '../api';

export const especialidadeService = {
  // LISTAR TODAS
  async listar() {
    const response = await api.get('/especialidade');
    return response.data;
  },

  // LISTAR ATIVAS (para dropdowns)
  async listarAtivas() {
    const response = await api.get('/especialidade/ativas');
    return response.data;
  },

  // CRIAR
  async criar(especialidade) {
    const response = await api.post('/especialidade', especialidade);
    return response.data;
  },

  // INICIALIZAR DADOS PADRÃO
  async inicializar() {
    const response = await api.post('/especialidade/initialize');
    return response.data;
  }
};

// services/regiaoService.js
import api from '../api';

export const regiaoService = {
  // LISTAR ATIVAS (para dropdowns)
  async listarAtivas() {
    const response = await api.get('/regiao/ativas');
    return response.data;
  },

  // BUSCAR POR CIDADE
  async buscarPorCidade(cidade) {
    const response = await api.get(`/regiao/cidade/${cidade}`);
    return response.data;
  },

  // CRIAR
  async criar(regiao) {
    const response = await api.post('/regiao', regiao);
    return response.data;
  },

  // INICIALIZAR DADOS PADRÃO
  async inicializar() {
    const response = await api.post('/regiao/initialize');
    return response.data;
  }
};

// hooks/useAuth.js - Hook personalizado para autenticação
import { useState, useEffect, createContext, useContext } from 'react';
import { usuarioService } from '../services/usuarioService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar se há usuário logado no localStorage
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = async (email, senha) => {
    try {
      const userData = await usuarioService.login(email, senha);
      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
};

// components/Login.jsx - Componente de Login
import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(email, senha);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h2>Login</h2>
        
        {error && <div className="error">{error}</div>}
        
        <div>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        
        <div>
          <label>Senha:</label>
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
};

export default Login;

// components/ClienteForm.jsx - Formulário de Cliente
import React, { useState } from 'react';
import { clienteService } from '../services/clienteService';

const ClienteForm = ({ onSuccess }) => {
  const [formData, setFormData] = useState({
    cpf: '',
    dataNascimento: '',
    statusCliente: 'ATIVO',
    usuario: {
      nome: '',
      email: '',
      senha: '',
      nivelAcesso: 'USER',
      statusUsuario: 'ATIVO'
    }
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    if (name.startsWith('usuario.')) {
      const field = name.split('.')[1];
      setFormData(prev => ({
        ...prev,
        usuario: {
          ...prev.usuario,
          [field]: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await clienteService.criar(formData);
      onSuccess?.();
      // Limpar formulário
      setFormData({
        cpf: '',
        dataNascimento: '',
        statusCliente: 'ATIVO',
        usuario: {
          nome: '',
          email: '',
          senha: '',
          nivelAcesso: 'USER',
          statusUsuario: 'ATIVO'
        }
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>Cadastrar Cliente</h3>
      
      {error && <div className="error">{error}</div>}
      
      <div>
        <label>CPF:</label>
        <input
          type="text"
          name="cpf"
          value={formData.cpf}
          onChange={handleChange}
          placeholder="11144477735"
          required
        />
      </div>
      
      <div>
        <label>Data de Nascimento:</label>
        <input
          type="date"
          name="dataNascimento"
          value={formData.dataNascimento}
          onChange={handleChange}
          required
        />
      </div>
      
      <div>
        <label>Nome:</label>
        <input
          type="text"
          name="usuario.nome"
          value={formData.usuario.nome}
          onChange={handleChange}
          required
        />
      </div>
      
      <div>
        <label>Email:</label>
        <input
          type="email"
          name="usuario.email"
          value={formData.usuario.email}
          onChange={handleChange}
          required
        />
      </div>
      
      <div>
        <label>Senha:</label>
        <input
          type="password"
          name="usuario.senha"
          value={formData.usuario.senha}
          onChange={handleChange}
          required
        />
      </div>
      
      <button type="submit" disabled={loading}>
        {loading ? 'Salvando...' : 'Salvar Cliente'}
      </button>
    </form>
  );
};

export default ClienteForm;

// hooks/useEspecialidades.js - Hook para especialidades
import { useState, useEffect } from 'react';
import { especialidadeService } from '../services/especialidadeService';

export const useEspecialidades = () => {
  const [especialidades, setEspecialidades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const carregarEspecialidades = async () => {
      try {
        const data = await especialidadeService.listarAtivas();
        setEspecialidades(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    carregarEspecialidades();
  }, []);

  return { especialidades, loading, error };
};

// Exemplo de uso do hook em componente
/*
const EspecialidadeSelect = ({ value, onChange }) => {
  const { especialidades, loading, error } = useEspecialidades();

  if (loading) return <div>Carregando...</div>;
  if (error) return <div>Erro: {error}</div>;

  return (
    <select value={value} onChange={onChange}>
      <option value="">Selecione uma especialidade</option>
      {especialidades.map(esp => (
        <option key={esp.id} value={esp.id}>
          {esp.nome}
        </option>
      ))}
    </select>
  );
};
*/