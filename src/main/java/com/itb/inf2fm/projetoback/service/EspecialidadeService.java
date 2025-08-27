// Service para Especialidade
// Contém regras de negócio e validações para operações de especialidade
// Integração com Frontend: métodos deste service são usados pelos controllers REST
// Flutter/ReactJS consomem endpoints que dependem desta lógica
package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.model.Especialidade;
import com.itb.inf2fm.projetoback.repository.EspecialidadeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {

    private static final Logger logger = LoggerFactory.getLogger(EspecialidadeService.class);
    private static final String STATUS_ATIVO = "ATIVO";

    private final EspecialidadeRepository especialidadeRepository;
    
    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    public Especialidade save(Especialidade especialidade) {
        try {
            if (especialidade.getStatusEspecialidade() == null || especialidade.getStatusEspecialidade().isEmpty()) {
                especialidade.setStatusEspecialidade(STATUS_ATIVO);
            }
            return especialidadeRepository.save(especialidade);
        } catch (DataAccessException e) {
            logger.error("Erro ao salvar especialidade", e);
            Especialidade errorEspecialidade = new Especialidade();
            errorEspecialidade.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            errorEspecialidade.setValid(false);
            return errorEspecialidade;
        }
    }

    public Especialidade findById(Long id) {
        try {
            Optional<Especialidade> especialidade = especialidadeRepository.findById(id);
            return especialidade.orElse(null);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar especialidade por ID: {}", id, e);
            Especialidade especialidade = new Especialidade();
            especialidade.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            especialidade.setValid(false);
            return especialidade;
        }
    }

    public Especialidade findByNome(String nome) {
        try {
            Optional<Especialidade> especialidade = especialidadeRepository.findByNome(nome);
            return especialidade.orElse(null);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar especialidade por nome: {}", nome, e);
            Especialidade especialidade = new Especialidade();
            especialidade.setMensagemErro("Erro interno do sistema. Tente novamente mais tarde!");
            especialidade.setValid(false);
            return especialidade;
        }
    }

    public List<Especialidade> findAll() {
        try {
            return especialidadeRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar todas as especialidades", e);
            return List.of();
        }
    }

    public List<Especialidade> findByStatus(String status) {
        try {
            return especialidadeRepository.findByStatusEspecialidade(status);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar especialidades por status: {}", status, e);
            return List.of();
        }
    }

    public void delete(Long id) {
        try {
            especialidadeRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Erro ao deletar especialidade com ID: {}", id, e);
            throw e;
        }
    }

    public boolean existsByNome(String nome) {
        try {
            return especialidadeRepository.existsByNome(nome);
        } catch (DataAccessException e) {
            logger.error("Erro ao verificar existência da especialidade por nome: {}", nome, e);
            return false;
        }
    }

    // Método para inicializar especialidades padrão
    public void initializeDefaultEspecialidades() {
        String[] especialidadesDefault = {"DSA", "Hardware", "Redes", "Software", "Banco de Dados"};
        
        for (String nome : especialidadesDefault) {
            if (!existsByNome(nome)) {
                Especialidade especialidade = new Especialidade();
                especialidade.setNome(nome);
                especialidade.setDescricao("Especialidade em " + nome);
                especialidade.setStatusEspecialidade(STATUS_ATIVO);
                save(especialidade);
            }
        }
    }
}
