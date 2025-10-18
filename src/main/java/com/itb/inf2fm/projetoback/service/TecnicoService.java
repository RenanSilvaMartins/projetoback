// Service para Técnico
// Contém regras de negócio e validações para operações de técnico
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.*;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.model.TecnicoRegiao;
import com.itb.inf2fm.projetoback.model.Regiao;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRegiaoRepository;
import com.itb.inf2fm.projetoback.repository.RegiaoRepository;
import com.itb.inf2fm.projetoback.util.CrudValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TecnicoService {

    private static final Logger logger = LoggerFactory.getLogger(TecnicoService.class);
    private static final String STATUS_ATIVO = "ATIVO";
    private static final String STATUS_INATIVO = "INATIVO";
    private static final String NIVEL_USER = "USER";

    private final TecnicoRepository tecnicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TecnicoRegiaoRepository tecnicoRegiaoRepository;
    private final RegiaoRepository regiaoRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public TecnicoService(TecnicoRepository tecnicoRepository, 
                         UsuarioRepository usuarioRepository,
                         TecnicoRegiaoRepository tecnicoRegiaoRepository,
                         RegiaoRepository regiaoRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tecnicoRegiaoRepository = tecnicoRegiaoRepository;
        this.regiaoRepository = regiaoRepository;
    }

    @Transactional
    public Tecnico salvarTecnico(Tecnico tecnico) {
        logger.info("Iniciando salvamento de técnico");
        
        if (tecnico == null || tecnico.getUsuario() == null) {
            throw new IllegalArgumentException("Técnico e usuário são obrigatórios");
        }
        
        if (tecnico.getUsuario().getSenha() == null || tecnico.getUsuario().getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do usuário é obrigatória");
        }
        
        logger.debug("CPF: {}, Email: {}", tecnico.getCpfCnpj(), tecnico.getUsuario().getEmail());
        
        // Criptografa a senha do usuário
        tecnico.getUsuario().setSenha(BCrypt.hashpw(tecnico.getUsuario().getSenha(), BCrypt.gensalt()));
        
        // Define valores padrão se não estiverem definidos
        if (tecnico.getUsuario().getDataCadastro() == null) {
            tecnico.getUsuario().setDataCadastro(LocalDateTime.now());
        }
        if (tecnico.getUsuario().getStatusUsuario() == null) {
            tecnico.getUsuario().setStatusUsuario(STATUS_ATIVO);
        }
        if (tecnico.getUsuario().getNivelAcesso() == null) {
            tecnico.getUsuario().setNivelAcesso(NIVEL_USER);
        }
        if (tecnico.getStatusTecnico() == null) {
            tecnico.setStatusTecnico(STATUS_ATIVO);
        }
        
        // Guarda as regiões temporariamente e remove do técnico antes de salvar
        List<Regiao> regioesTemp = tecnico.getRegioes();
        tecnico.setRegioes(null);
        
        // Salva o usuario primeiro para obter o ID
        logger.info("Salvando usuário no banco de dados");
        Usuario usuarioSalvo = usuarioRepository.save(tecnico.getUsuario());
        logger.info("Usuário salvo com ID: {}", usuarioSalvo.getId());
        
        // Define o ID do técnico como o ID do usuário (chave primária compartilhada)
        tecnico.setId(usuarioSalvo.getId());
        tecnico.setUsuario(usuarioSalvo);
        
        logger.info("Salvando técnico no banco de dados com ID: {}", tecnico.getId());
        
        // Usa SQL nativo para fazer o INSERT com IDENTITY_INSERT
        String sql = "SET IDENTITY_INSERT Tecnico ON; " +
                     "INSERT INTO Tecnico (id, cpf_cnpj, dataNascimento, telefone, cep, numeroResidencia, complemento, descricao, especialidade, statusTecnico, usuario_id) " +
                     "VALUES (:id, :cpf, :dataNasc, :tel, :cep, :num, :comp, :desc, :esp, :status, :userId); " +
                     "SET IDENTITY_INSERT Tecnico OFF";
        
        entityManager.createNativeQuery(sql)
            .setParameter("id", tecnico.getId())
            .setParameter("cpf", tecnico.getCpfCnpj())
            .setParameter("dataNasc", tecnico.getDataNascimento())
            .setParameter("tel", tecnico.getTelefone())
            .setParameter("cep", tecnico.getCep())
            .setParameter("num", tecnico.getNumeroResidencia())
            .setParameter("comp", tecnico.getComplemento())
            .setParameter("desc", tecnico.getDescricao())
            .setParameter("esp", tecnico.getEspecialidade())
            .setParameter("status", tecnico.getStatusTecnico())
            .setParameter("userId", usuarioSalvo.getId())
            .executeUpdate();
        
        logger.info("Técnico salvo com sucesso");
        
        // Processa as regiões
        List<Regiao> regioesRecebidas = regioesTemp;
        
        if (regioesRecebidas != null && !regioesRecebidas.isEmpty()) {
            logger.info("Processando {} região(ões)", regioesRecebidas.size());
            for (Regiao regiao : regioesRecebidas) {
                Regiao existente = null;
                
                // Se a região tem ID, busca por ID
                if (regiao.getId() != null) {
                    logger.info("Buscando região com ID: {}", regiao.getId());
                    existente = regiaoRepository.findById(regiao.getId()).orElse(null);
                    if (existente != null) {
                        logger.info("Região encontrada: {} - {}", existente.getNome(), existente.getCidade());
                    } else {
                        logger.warn("Região com ID {} não encontrada", regiao.getId());
                        throw new IllegalArgumentException("Região com ID " + regiao.getId() + " não encontrada");
                    }
                } 
                // Se não tem ID mas tem nome e cidade, busca por nome e cidade
                else if (regiao.getNome() != null && regiao.getCidade() != null) {
                    logger.info("Buscando região por nome '{}' e cidade '{}'", regiao.getNome(), regiao.getCidade());
                    existente = regiaoRepository.findByNomeAndCidade(regiao.getNome(), regiao.getCidade()).orElse(null);
                }
                
                // Se não encontrou, valida e salva nova região
                if (existente == null) {
                    if (regiao.getNome() == null || regiao.getCidade() == null) {
                        throw new IllegalArgumentException("Para criar uma nova região, nome e cidade são obrigatórios");
                    }
                    logger.info("Criando nova região: {} - {}", regiao.getNome(), regiao.getCidade());
                    // Define valores padrão para nova região
                    if (regiao.getDescricao() == null) {
                        regiao.setDescricao("");
                    }
                    if (regiao.getStatusRegiao() == null) {
                        regiao.setStatusRegiao("ATIVO");
                    }
                    existente = regiaoRepository.save(regiao);
                    logger.info("Nova região criada com ID: {}", existente.getId());
                }
                
                logger.info("Criando associação TecnicoRegiao");
                TecnicoRegiao tecnicoRegiao = new TecnicoRegiao();
                tecnicoRegiao.setTecnico(tecnico);
                tecnicoRegiao.setRegiao(existente);
                tecnicoRegiao.setStatusTecnicoRegiao("ATIVO");
                tecnicoRegiaoRepository.save(tecnicoRegiao);
                logger.info("Associação TecnicoRegiao criada com sucesso");
            }
        }
        
        // Retorne o técnico já salvo
        return tecnico;
    }

    public Optional<Tecnico> findByCpfCnpj(String cpfCnpj) {
        return tecnicoRepository.findByCpfCnpj(cpfCnpj);
    }

    public Optional<Tecnico> findByEmail(String email) {
        return tecnicoRepository.findByUsuarioEmail(email);
    }

    @Transactional
    public Tecnico save(Tecnico tecnico) {
        logger.debug("Salvando técnico: {}", tecnico.getId());
        
        if (tecnico.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório para o técnico");
        }
        
        // Se o usuário tem ID, verificar se existe e criar referência
        if (tecnico.getUsuario().getId() != null) {
            Long usuarioId = tecnico.getUsuario().getId();
            logger.debug("Verificando se usuário com ID {} existe", usuarioId);
            
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isPresent()) {
                Usuario usuarioExistente = usuarioOpt.get();
                logger.debug("Usuário encontrado: {}", usuarioExistente.getEmail());
                tecnico.setUsuario(usuarioExistente);
                tecnico.setId(usuarioExistente.getId());
            } else {
                logger.error("Usuário não encontrado com ID: {}", usuarioId);
                throw new ResourceNotFoundException("Usuário", "id", usuarioId);
            }
        }
        
        // Define valor padrão para statusTecnico se não estiver definido
        if (tecnico.getStatusTecnico() == null) {
            tecnico.setStatusTecnico(STATUS_ATIVO);
        }
        
        logger.info("Salvando técnico com ID: {}", tecnico.getId());
        return tecnicoRepository.save(tecnico);
    }

    public List<Tecnico> findAll() {
        return tecnicoRepository.findAll();
    }

    public Optional<Tecnico> findById(Long id) {
        return tecnicoRepository.findById(id);
    }

    public List<Tecnico> findByStatus(String statusTecnico) {
        return tecnicoRepository.findByStatusTecnico(statusTecnico);
    }

    @Transactional
    public Tecnico update(Tecnico tecnico) {
        // Validações básicas
        if (tecnico == null) {
            throw new ValidationException("Técnico não pode ser nulo");
        }
        
        // Valida ID
        CrudValidationUtils.validateId(tecnico.getId(), "Técnico");
        
        // Busca técnico existente
        Tecnico existingTecnico = CrudValidationUtils.validateResourceExists(
            () -> tecnicoRepository.findById(tecnico.getId()).orElse(null),
            "Técnico", tecnico.getId()
        );
        
        // Validações de campos que serão atualizados
        if (tecnico.getCpfCnpj() != null) {
            CrudValidationUtils.validateCpf(tecnico.getCpfCnpj());
            // Verifica se CPF/CNPJ não está em uso por outro técnico
            Optional<Tecnico> tecnicoComCpf = findByCpfCnpj(tecnico.getCpfCnpj());
            if (tecnicoComCpf.isPresent() && !tecnicoComCpf.get().getId().equals(tecnico.getId())) {
                throw new DuplicateResourceException("CPF/CNPJ", tecnico.getCpfCnpj());
            }
        }
        
        if (tecnico.getUsuario() != null && tecnico.getUsuario().getEmail() != null) {
            CrudValidationUtils.validateEmail(tecnico.getUsuario().getEmail());
            // Verifica se email não está em uso por outro usuário
            Optional<Tecnico> tecnicoComEmail = findByEmail(tecnico.getUsuario().getEmail());
            if (tecnicoComEmail.isPresent() && !tecnicoComEmail.get().getId().equals(tecnico.getId())) {
                throw new DuplicateResourceException("Email", tecnico.getUsuario().getEmail());
            }
        }
        
        try {
            // Atualiza dados do usuário apenas se não forem nulos
            if (tecnico.getUsuario() != null) {
                if (tecnico.getUsuario().getNome() != null) {
                    existingTecnico.getUsuario().setNome(tecnico.getUsuario().getNome());
                }
                if (tecnico.getUsuario().getEmail() != null) {
                    existingTecnico.getUsuario().setEmail(tecnico.getUsuario().getEmail());
                }
                
                // Só atualiza a senha se uma nova foi fornecida
                if (tecnico.getUsuario().getSenha() != null && !tecnico.getUsuario().getSenha().isEmpty()) {
                    existingTecnico.getUsuario().setSenha(BCrypt.hashpw(tecnico.getUsuario().getSenha(), BCrypt.gensalt()));
                }
            }
            
            // Atualiza dados específicos do técnico apenas se não forem nulos
            if (tecnico.getCpfCnpj() != null) {
                existingTecnico.setCpfCnpj(tecnico.getCpfCnpj());
            }
            if (tecnico.getDataNascimento() != null) {
                existingTecnico.setDataNascimento(tecnico.getDataNascimento());
            }
            if (tecnico.getTelefone() != null) {
                existingTecnico.setTelefone(tecnico.getTelefone());
            }
            if (tecnico.getCep() != null) {
                existingTecnico.setCep(tecnico.getCep());
            }
            if (tecnico.getNumeroResidencia() != null) {
                existingTecnico.setNumeroResidencia(tecnico.getNumeroResidencia());
            }
            if (tecnico.getComplemento() != null) {
                existingTecnico.setComplemento(tecnico.getComplemento());
            }
            if (tecnico.getDescricao() != null) {
                existingTecnico.setDescricao(tecnico.getDescricao());
            }
            if (tecnico.getStatusTecnico() != null) {
                existingTecnico.setStatusTecnico(tecnico.getStatusTecnico());
            }
            
            return tecnicoRepository.save(existingTecnico);
        } catch (DataAccessException e) {
            throw new DatabaseException("atualizar técnico", "Erro ao atualizar técnico no banco de dados");
        }
    }

    @Transactional
    public void delete(Long id) {
        // Valida ID
        CrudValidationUtils.validateId(id, "Técnico");
        
        // Verifica se técnico existe
        CrudValidationUtils.validateResourceExists(
            () -> tecnicoRepository.existsById(id) ? "exists" : null,
            "Técnico", id
        );
        
        try {
            // Remove relacionamentos primeiro
            tecnicoRegiaoRepository.deleteByTecnicoId(id);
            // Remove o técnico
            tecnicoRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("deletar técnico", "Erro ao deletar técnico do banco de dados");
        }
    }

    @Transactional
    public Tecnico inativar(Long id) {
        // Valida ID
        CrudValidationUtils.validateId(id, "Técnico");
        
        // Busca técnico existente
        Tecnico tecnico = CrudValidationUtils.validateResourceExists(
            () -> tecnicoRepository.findById(id).orElse(null),
            "Técnico", id
        );
        
        try {
            tecnico.setStatusTecnico(STATUS_INATIVO);
            tecnico.getUsuario().setStatusUsuario(STATUS_INATIVO);
            return tecnicoRepository.save(tecnico);
        } catch (DataAccessException e) {
            throw new DatabaseException("inativar técnico", "Erro ao inativar técnico no banco de dados");
        }
    }

    public boolean existsByCpfCnpj(String cpfCnpj) {
        return tecnicoRepository.existsByCpfCnpj(cpfCnpj);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // Métodos para gerenciar regiões do técnico
    @Transactional
    public void adicionarRegiao(Long tecnicoId, Long regiaoId) {
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico", "id", tecnicoId));
        
        var regiao = regiaoRepository.findById(regiaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Região", "id", regiaoId));
        
        TecnicoRegiao tecnicoRegiao = new TecnicoRegiao();
        tecnicoRegiao.setTecnico(tecnico);
        tecnicoRegiao.setRegiao(regiao);
        tecnicoRegiao.setStatusTecnicoRegiao(STATUS_ATIVO);
        tecnicoRegiaoRepository.save(tecnicoRegiao);
    }

    @Transactional
    public void removerRegiao(Long tecnicoId, Long regiaoId) {
        if (!tecnicoRepository.existsById(tecnicoId)) {
            throw new ResourceNotFoundException("Técnico", "id", tecnicoId);
        }
        tecnicoRegiaoRepository.deleteByTecnicoIdAndRegiaoId(tecnicoId, regiaoId);
    }

    public List<TecnicoRegiao> getRegioesByTecnico(Long tecnicoId) {
        if (!tecnicoRepository.existsById(tecnicoId)) {
            throw new ResourceNotFoundException("Técnico", "id", tecnicoId);
        }
        return tecnicoRegiaoRepository.findByTecnicoId(tecnicoId);
    }

    public List<Tecnico> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return tecnicoRepository.findByUsuarioNomeContainingIgnoreCase(nome.trim());
    }
    
    public List<String> getEspecialidades() {
        return tecnicoRepository.findDistinctEspecialidades();
    }
}
