// Service para Usuário
// Contém regras de negócio e validações para operações de usuário e autenticação
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.*;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.service.CacheService;
import com.itb.inf2fm.projetoback.util.CrudValidationUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private static final String STATUS_ATIVO = "ATIVO";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptService passwordEncryptService;
    private final CacheService cacheService;
    
    public UsuarioService(UsuarioRepository usuarioRepository, 
                         PasswordEncryptService passwordEncryptService,
                         CacheService cacheService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncryptService = passwordEncryptService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new ValidationException("Usuário não pode ser nulo");
        }
        
        // Validações de campos obrigatórios
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("nome", usuario.getNome());
        requiredFields.put("email", usuario.getEmail());
        if (usuario.getId() == null) {
            requiredFields.put("senha", usuario.getSenha());
        }
        
        CrudValidationUtils.validateRequiredFields(requiredFields);
        CrudValidationUtils.validateEmail(usuario.getEmail());
        
        // Verifica duplicatas
        CrudValidationUtils.validateResourceNotExists(
            () -> existsByEmail(usuario.getEmail()),
            "Email", usuario.getEmail()
        );
        
        try {
            // Criptografa senha se fornecida
            if (usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty()) {
                usuario.setSenha(passwordEncryptService.encryptPassword(usuario.getSenha()));
            }
            
            // Define valores padrão
            if (usuario.getDataCadastro() == null) {
                usuario.setDataCadastro(LocalDateTime.now());
            }
            if (usuario.getStatusUsuario() == null || usuario.getStatusUsuario().trim().isEmpty()) {
                usuario.setStatusUsuario(STATUS_ATIVO);
            }
            if (usuario.getNivelAcesso() == null || usuario.getNivelAcesso().trim().isEmpty()) {
                usuario.setNivelAcesso("USER");
            }
            
            Usuario saved = usuarioRepository.save(usuario);
            
            // Invalidar cache após salvar
            if (saved.getEmail() != null) {
                cacheService.remove("user_email_" + saved.getEmail());
            }
            
            return saved;
        } catch (DataAccessException e) {
            throw new DatabaseException("salvar usuário", "Erro ao salvar usuário no banco de dados");
        }
    }
    
    private void validateUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (usuario.getId() == null && (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty())) {
            throw new IllegalArgumentException("Senha é obrigatória para novos usuários");
        }
    }

    public Usuario findById(Long id) {
        CrudValidationUtils.validateId(id, "Usuário");
        
        try {
            String cacheKey = "user_id_" + id;
            Usuario cached = cacheService.get(cacheKey, Usuario.class);
            if (cached != null) {
                return cached;
            }
            
            return CrudValidationUtils.validateResourceExists(
                () -> usuarioRepository.findById(id).orElse(null),
                "Usuário", id
            );
        } catch (DataAccessException e) {
            throw new DatabaseException("buscar usuário", "Erro ao buscar usuário no banco de dados");
        }
    }

    public Usuario findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        try {
            String cacheKey = "user_email_" + email.trim().toLowerCase();
            Usuario cached = cacheService.get(cacheKey, Usuario.class);
            if (cached != null) {
                return cached;
            }
            
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email.trim());
            if (usuario.isPresent()) {
                cacheService.put(cacheKey, usuario.get());
                return usuario.get();
            }
            return null;
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Erro de acesso aos dados ao buscar usuário por email");
            return null;
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar usuário por email: {}", e.getMessage());
            return null;
        }
    }

    public List<Usuario> findAll() {
        try {
            return usuarioRepository.findAll();
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Erro de acesso aos dados ao buscar todos os usuários");
            return List.of();
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar todos os usuários: {}", e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public void delete(Long id) {
        CrudValidationUtils.validateId(id, "Usuário");
        
        CrudValidationUtils.validateResourceExists(
            () -> usuarioRepository.existsById(id) ? "exists" : null,
            "Usuário", id
        );
        
        try {
            usuarioRepository.deleteById(id);
        } catch (DataAccessException e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                throw new InvalidOperationException("deletar usuário", 
                    "Usuário possui registros dependentes (clientes, técnicos, etc.)");
            }
            throw new DatabaseException("deletar usuário", "Erro ao deletar usuário do banco de dados");
        }
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        try {
            return usuarioRepository.existsByEmail(email.trim());
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Erro de acesso aos dados ao verificar existência de email");
            return false;
        } catch (RuntimeException e) {
            logger.error("Erro ao verificar existência de email: {}", e.getMessage());
            return false;
        }
    }

    public Usuario authenticate(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setMensagemErro("Email e senha são obrigatórios");
            usuario.setIsValid(false);
            return usuario;
        }
        
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            
            if (usuario.isPresent()) {
                Usuario user = usuario.get();
                
                // Verifica se a senha está correta
                if (passwordEncryptService.checkPassword(senha, user.getSenha())) {
                    // Verifica se o usuário está ativo
                    if (STATUS_ATIVO.equals(user.getStatusUsuario())) {
                        return user;
                    } else {
                        user.setMensagemErro("Usuário inativo!");
                        user.setIsValid(false);
                        return user;
                    }
                } else {
                    user.setMensagemErro("Email ou senha incorretos!");
                    user.setIsValid(false);
                    return user;
                }
            } else {
                Usuario user = new Usuario();
                user.setMensagemErro("Email ou senha incorretos!");
                user.setIsValid(false);
                return user;
            }
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Erro de acesso aos dados ao autenticar usuário", e);
            Usuario usuario = new Usuario();
            usuario.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            usuario.setIsValid(false);
            return usuario;
        } catch (RuntimeException e) {
            logger.error("Erro ao autenticar usuário: {}", e.getMessage(), e);
            Usuario usuario = new Usuario();
            usuario.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            usuario.setIsValid(false);
            return usuario;
        }
    }
    
    public List<Usuario> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return usuarioRepository.findByNomeContainingIgnoreCase(nome.trim());
    }
}
