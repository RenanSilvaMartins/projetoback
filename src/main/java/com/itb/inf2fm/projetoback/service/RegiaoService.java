// Service para Região
// Contém regras de negócio e validações para operações de região
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.*;
import com.itb.inf2fm.projetoback.model.Regiao;
import com.itb.inf2fm.projetoback.repository.RegiaoRepository;
import com.itb.inf2fm.projetoback.util.CrudValidationUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RegiaoService {

    private static final Logger logger = LoggerFactory.getLogger(RegiaoService.class);
    private static final String STATUS_ATIVO = "ATIVO";

    @Autowired
    RegiaoRepository regiaoRepository;

    @Transactional
    public Regiao save(Regiao regiao) {
        if (regiao == null) {
            throw new ValidationException("Região não pode ser nula");
        }
        
        // Validações de campos obrigatórios
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("nome", regiao.getNome());
        requiredFields.put("cidade", regiao.getCidade());
        
        CrudValidationUtils.validateRequiredFields(requiredFields);
        
        // Verifica duplicatas
        CrudValidationUtils.validateResourceNotExists(
            () -> existsByNome(regiao.getNome()),
            "Nome da região", regiao.getNome()
        );
        
        try {
            if (regiao.getStatusRegiao() == null || regiao.getStatusRegiao().isEmpty()) {
                regiao.setStatusRegiao(STATUS_ATIVO);
            }
            return regiaoRepository.save(regiao);
        } catch (DataAccessException e) {
            throw new DatabaseException("salvar região", "Erro ao salvar região no banco de dados");
        }
    }

    public Regiao findById(Long id) {
        CrudValidationUtils.validateId(id, "Região");
        
        try {
            return CrudValidationUtils.validateResourceExists(
                () -> regiaoRepository.findById(id).orElse(null),
                "Região", id
            );
        } catch (DataAccessException e) {
            throw new DatabaseException("buscar região", "Erro ao buscar região no banco de dados");
        }
    }

    public Regiao findByNome(String nome) {
        try {
            Optional<Regiao> regiao = regiaoRepository.findByNome(nome);
            return regiao.orElse(null);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar região por nome: {}", nome, e);
            Regiao regiao = new Regiao();
            regiao.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            regiao.setValid(false);
            return regiao;
        }
    }

    public List<Regiao> findAll() {
        try {
            return regiaoRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar todas as regiões", e);
            return List.of();
        }
    }

    public List<Regiao> findByStatus(String status) {
        try {
            return regiaoRepository.findByStatusRegiao(status);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar regiões por status: {}", status, e);
            return List.of();
        }
    }

    public List<Regiao> findByCidade(String cidade) {
        try {
            return regiaoRepository.findByCidade(cidade);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar regiões por cidade: {}", cidade, e);
            return List.of();
        }
    }

    @Transactional
    public void delete(Long id) {
        CrudValidationUtils.validateId(id, "Região");
        
        CrudValidationUtils.validateResourceExists(
            () -> regiaoRepository.existsById(id) ? "exists" : null,
            "Região", id
        );
        
        try {
            regiaoRepository.deleteById(id);
        } catch (DataAccessException e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                throw new InvalidOperationException("deletar região", 
                    "Região possui técnicos associados");
            }
            throw new DatabaseException("deletar região", "Erro ao deletar região do banco de dados");
        }
    }

    public boolean existsByNome(String nome) {
        try {
            return regiaoRepository.existsByNome(nome);
        } catch (DataAccessException e) {
            logger.error("Erro ao verificar existência da região por nome: {}", nome, e);
            return false;
        }
    }

    // Método para inicializar regiões padrão
    public void initializeDefaultRegioes() {
        String[] regioesDefault = {"Norte", "Sul", "Leste", "Oeste"};
        
        for (String nome : regioesDefault) {
            if (!existsByNome(nome)) {
                Regiao regiao = new Regiao();
                regiao.setCidade(nome); // Conforme o INSERT do seu script
                regiao.setNome(nome);
                regiao.setDescricao("Região " + nome + " da cidade");
                regiao.setStatusRegiao(STATUS_ATIVO);
                save(regiao);
            }
        }
    }
}
