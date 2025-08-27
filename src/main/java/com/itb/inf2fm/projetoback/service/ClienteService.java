// Service para Cliente
// Contém regras de negócio e validações para operações de cliente
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exceptions.NotFound;
import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Cliente salvarCliente(Cliente cliente) {
        if (cliente == null || cliente.getUsuario() == null) {
            throw new IllegalArgumentException("Cliente e usuário são obrigatórios");
        }
        
        // Valida e criptografa a senha do usuário
        if (cliente.getUsuario().getSenha() == null || cliente.getUsuario().getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        cliente.getUsuario().setSenha(BCrypt.hashpw(cliente.getUsuario().getSenha(), BCrypt.gensalt()));
        
        // Define valores padrão se não estiverem definidos
        if (cliente.getUsuario().getDataCadastro() == null) {
            cliente.getUsuario().setDataCadastro(LocalDateTime.now());
        }
        if (cliente.getUsuario().getStatusUsuario() == null) {
            cliente.getUsuario().setStatusUsuario("ATIVO");
        }
        if (cliente.getUsuario().getNivelAcesso() == null) {
            cliente.getUsuario().setNivelAcesso("USER");
        }
        if (cliente.getStatusCliente() == null) {
            cliente.setStatusCliente("ATIVO");
        }
        
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> findByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByUsuarioEmail(email);
    }

    @Transactional
    public Cliente save(Cliente cliente) {
        try {
            // Valida dados básicos
            if (cliente == null) {
                throw new IllegalArgumentException("Cliente não pode ser nulo");
            }
            
            // Se tem usuário, usa o método completo
            if (cliente.getUsuario() != null) {
                return salvarCliente(cliente);
            }
            
            // Senão, salva diretamente
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            cliente.setMensagemErro("Erro ao salvar cliente: " + e.getMessage());
            cliente.setValid(false);
            return cliente;
        }
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> findByStatus(String statusCliente) {
        return clienteRepository.findByStatusCliente(statusCliente);
    }

    @Transactional
    public Cliente update(Cliente cliente) {
        Cliente existingCliente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new NotFound("Cliente não encontrado"));
        
        // Atualiza dados do usuário
        if (cliente.getUsuario() != null) {
            if (cliente.getUsuario().getNome() != null) {
                existingCliente.getUsuario().setNome(cliente.getUsuario().getNome());
            }
            if (cliente.getUsuario().getEmail() != null) {
                existingCliente.getUsuario().setEmail(cliente.getUsuario().getEmail());
            }
            
            // Só atualiza a senha se uma nova foi fornecida
            if (cliente.getUsuario().getSenha() != null && !cliente.getUsuario().getSenha().isEmpty()) {
                existingCliente.getUsuario().setSenha(BCrypt.hashpw(cliente.getUsuario().getSenha(), BCrypt.gensalt()));
            }
        }
        
        // Atualiza dados específicos do cliente apenas se não forem nulos
        if (cliente.getCpf() != null) {
            existingCliente.setCpf(cliente.getCpf());
        }
        if (cliente.getDataNascimento() != null) {
            existingCliente.setDataNascimento(cliente.getDataNascimento());
        }
        if (cliente.getStatusCliente() != null) {
            existingCliente.setStatusCliente(cliente.getStatusCliente());
        }
        
        return clienteRepository.save(existingCliente);
    }

    @Transactional
    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        try {
            if (clienteRepository.existsById(id)) {
                clienteRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean inativar(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        try {
            Optional<Cliente> clienteOpt = clienteRepository.findById(id);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                cliente.setStatusCliente("INATIVO");
                if (cliente.getUsuario() != null) {
                    cliente.getUsuario().setStatusUsuario("INATIVO");
                }
                clienteRepository.save(cliente);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsByCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        return clienteRepository.existsByCpf(cpf);
    }

    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }
        return usuarioRepository.existsByEmail(email);
    }
}