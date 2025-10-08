// Service para Técnico
// Contém regras de negócio e validações para operações de técnico
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exceptions.NotFound;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.model.TecnicoRegiao;
import com.itb.inf2fm.projetoback.model.Regiao;

import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRegiaoRepository;
import com.itb.inf2fm.projetoback.repository.RegiaoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        if (tecnico == null || tecnico.getUsuario() == null) {
            throw new IllegalArgumentException("Técnico e usuário são obrigatórios");
        }
        
        if (tecnico.getUsuario().getSenha() == null || tecnico.getUsuario().getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do usuário é obrigatória");
        }
        
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
        
        // Salva o técnico normalmente
        Tecnico tecnicoSalvo = tecnicoRepository.save(tecnico);
        
        // Usa a lista de regiões recebida do frontend (apenas para leitura)
        List<Regiao> regioesRecebidas = tecnico.getRegioes();
        
        if (regioesRecebidas != null && !regioesRecebidas.isEmpty()) {
            for (Regiao regiao : regioesRecebidas) {
                Regiao existente = regiaoRepository.findByNomeAndCidade(regiao.getNome(), regiao.getCidade()).orElse(null);
                if (existente == null) {
                    existente = regiaoRepository.save(regiao);
                }
                TecnicoRegiao tecnicoRegiao = new TecnicoRegiao();
                tecnicoRegiao.setTecnico(tecnicoSalvo);
                tecnicoRegiao.setRegiao(existente);
                tecnicoRegiao.setStatusTecnicoRegiao("ATIVO");
                tecnicoRegiaoRepository.save(tecnicoRegiao);
            }
        }
        
        // Retorne o técnico já salvo
        return tecnicoSalvo;
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
                throw new NotFound("Usuário não encontrado com ID: " + usuarioId);
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
        if (tecnico == null || tecnico.getUsuario() == null) {
            throw new IllegalArgumentException("Técnico e usuário são obrigatórios");
        }
        
        Tecnico existingTecnico = tecnicoRepository.findById(tecnico.getId())
                .orElseThrow(() -> new NotFound("Técnico não encontrado"));
        
        // Atualiza dados do usuário
        existingTecnico.getUsuario().setNome(tecnico.getUsuario().getNome());
        existingTecnico.getUsuario().setEmail(tecnico.getUsuario().getEmail());
        
        // Só atualiza a senha se uma nova foi fornecida
        if (tecnico.getUsuario().getSenha() != null && !tecnico.getUsuario().getSenha().isEmpty()) {
            existingTecnico.getUsuario().setSenha(BCrypt.hashpw(tecnico.getUsuario().getSenha(), BCrypt.gensalt()));
        }
        
        // Atualiza dados específicos do técnico
        existingTecnico.setCpfCnpj(tecnico.getCpfCnpj());
        existingTecnico.setDataNascimento(tecnico.getDataNascimento());
        existingTecnico.setTelefone(tecnico.getTelefone());
        existingTecnico.setCep(tecnico.getCep());
        existingTecnico.setNumeroResidencia(tecnico.getNumeroResidencia());
        existingTecnico.setComplemento(tecnico.getComplemento());
        existingTecnico.setDescricao(tecnico.getDescricao());
        existingTecnico.setStatusTecnico(tecnico.getStatusTecnico());
        
        return tecnicoRepository.save(existingTecnico);
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Tecnico> tecnicoOptional = tecnicoRepository.findById(id);
        if (tecnicoOptional.isPresent()) {
            // Remove relacionamentos primeiro
            tecnicoRegiaoRepository.deleteByTecnicoId(id);
            // Remove o técnico
            tecnicoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean inativar(Long id) {
        Optional<Tecnico> tecnicoOptional = tecnicoRepository.findById(id);
        if (tecnicoOptional.isPresent()) {
            Tecnico tecnico = tecnicoOptional.get();
            tecnico.setStatusTecnico(STATUS_INATIVO);
            tecnico.getUsuario().setStatusUsuario(STATUS_INATIVO);
            tecnicoRepository.save(tecnico);
            return true;
        }
        return false;
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
                .orElseThrow(() -> new NotFound("Técnico não encontrado com ID: " + tecnicoId));
        
        var regiao = regiaoRepository.findById(regiaoId)
                .orElseThrow(() -> new NotFound("Região não encontrada com ID: " + regiaoId));
        
        TecnicoRegiao tecnicoRegiao = new TecnicoRegiao();
        tecnicoRegiao.setTecnico(tecnico);
        tecnicoRegiao.setRegiao(regiao);
        tecnicoRegiao.setStatusTecnicoRegiao(STATUS_ATIVO);
        tecnicoRegiaoRepository.save(tecnicoRegiao);
    }

    @Transactional
    public void removerRegiao(Long tecnicoId, Long regiaoId) {
        if (!tecnicoRepository.existsById(tecnicoId)) {
            throw new NotFound("Técnico não encontrado com ID: " + tecnicoId);
        }
        tecnicoRegiaoRepository.deleteByTecnicoIdAndRegiaoId(tecnicoId, regiaoId);
    }

    public List<TecnicoRegiao> getRegioesByTecnico(Long tecnicoId) {
        if (!tecnicoRepository.existsById(tecnicoId)) {
            throw new NotFound("Técnico não encontrado com ID: " + tecnicoId);
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
