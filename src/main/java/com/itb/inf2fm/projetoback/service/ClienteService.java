// Service para Cliente
// Contém regras de negócio e validações para operações de cliente
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.*;
import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.util.CrudValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Validações básicas
        if (cliente == null) {
            throw new ValidationException("Cliente não pode ser nulo");
        }
        
        // Valida campos obrigatórios
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("cpf", cliente.getCpf());
        requiredFields.put("dataNascimento", cliente.getDataNascimento());
        
        if (cliente.getUsuario() != null) {
            requiredFields.put("nome", cliente.getUsuario().getNome());
            requiredFields.put("email", cliente.getUsuario().getEmail());
            requiredFields.put("senha", cliente.getUsuario().getSenha());
        }
        
        CrudValidationUtils.validateRequiredFields(requiredFields);
        
        // Validações específicas
        CrudValidationUtils.validateCpf(cliente.getCpf());
        
        if (cliente.getUsuario() != null) {
            CrudValidationUtils.validateEmail(cliente.getUsuario().getEmail());
        }
        
        // Verifica duplicatas
        CrudValidationUtils.validateResourceNotExists(
            () -> existsByCpf(cliente.getCpf()),
            "CPF", cliente.getCpf()
        );
        
        if (cliente.getUsuario() != null && cliente.getUsuario().getEmail() != null) {
            CrudValidationUtils.validateResourceNotExists(
                () -> existsByEmail(cliente.getUsuario().getEmail()),
                "Email", cliente.getUsuario().getEmail()
            );
        }
        
        try {
            // Se tem usuário, usa o método completo
            if (cliente.getUsuario() != null) {
                return salvarCliente(cliente);
            } else {
                // Define status padrão se não estiver definido
                if (cliente.getStatusCliente() == null || cliente.getStatusCliente().trim().isEmpty()) {
                    cliente.setStatusCliente("ATIVO");
                }
                
                return clienteRepository.save(cliente);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException("salvar cliente", "Erro ao salvar cliente no banco de dados");
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
        // Valida ID
        CrudValidationUtils.validateId(cliente.getId(), "Cliente");
        
        // Busca cliente existente
        Cliente existingCliente = CrudValidationUtils.validateResourceExists(
            () -> clienteRepository.findById(cliente.getId()).orElse(null),
            "Cliente", cliente.getId()
        );
        
        // Validações de campos que serão atualizados
        if (cliente.getCpf() != null) {
            CrudValidationUtils.validateCpf(cliente.getCpf());
            // Verifica se CPF não está em uso por outro cliente
            Optional<Cliente> clienteComCpf = findByCpf(cliente.getCpf());
            if (clienteComCpf.isPresent() && !clienteComCpf.get().getId().equals(cliente.getId())) {
                throw new DuplicateResourceException("CPF", cliente.getCpf());
            }
        }
        
        if (cliente.getUsuario() != null && cliente.getUsuario().getEmail() != null) {
            CrudValidationUtils.validateEmail(cliente.getUsuario().getEmail());
            // Verifica se email não está em uso por outro usuário
            Optional<Cliente> clienteComEmail = findByEmail(cliente.getUsuario().getEmail());
            if (clienteComEmail.isPresent() && !clienteComEmail.get().getId().equals(cliente.getId())) {
                throw new DuplicateResourceException("Email", cliente.getUsuario().getEmail());
            }
        }
        
        try {
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
        } catch (DataAccessException e) {
            throw new DatabaseException("atualizar cliente", "Erro ao atualizar cliente no banco de dados");
        }
    }

    @Transactional
    public void delete(Long id) {
        // Valida ID
        CrudValidationUtils.validateId(id, "Cliente");
        
        // Verifica se cliente existe
        CrudValidationUtils.validateResourceExists(
            () -> clienteRepository.existsById(id) ? "exists" : null,
            "Cliente", id
        );
        
        try {
            clienteRepository.deleteById(id);
        } catch (DataAccessException e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                throw new InvalidOperationException("deletar cliente", 
                    "Cliente possui registros dependentes (agendamentos, etc.)");
            }
            throw new DatabaseException("deletar cliente", "Erro ao deletar cliente do banco de dados");
        }
    }

    @Transactional
    public Cliente inativar(Long id) {
        // Valida ID
        CrudValidationUtils.validateId(id, "Cliente");
        
        // Busca cliente existente
        Cliente cliente = CrudValidationUtils.validateResourceExists(
            () -> clienteRepository.findById(id).orElse(null),
            "Cliente", id
        );
        
        try {
            cliente.setStatusCliente("INATIVO");
            if (cliente.getUsuario() != null) {
                cliente.getUsuario().setStatusUsuario("INATIVO");
            }
            return clienteRepository.save(cliente);
        } catch (DataAccessException e) {
            throw new DatabaseException("inativar cliente", "Erro ao inativar cliente no banco de dados");
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
    
    public List<Cliente> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return clienteRepository.findByUsuarioNomeContainingIgnoreCase(nome);
    }
}