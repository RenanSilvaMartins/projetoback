package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private static final Logger logger = LoggerFactory.getLogger(ServicoService.class);
    private static final String STATUS_ATIVO = "ATIVO";

    private final ServicoRepository servicoRepository;
    
    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public Servico save(Servico servico) {
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não pode ser nulo");
        }
        try {
            if (servico.getStatusServico() == null || servico.getStatusServico().isEmpty()) {
                servico.setStatusServico(STATUS_ATIVO);
            }
            return servicoRepository.save(servico);
        } catch (DataAccessException e) {
            logger.error("Erro ao salvar serviço: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar serviço", e);
        }
    }

    public Servico findById(Long id) {
        try {
            Optional<Servico> servicoOpt = servicoRepository.findById(id);
            return servicoOpt.orElse(null);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar serviço por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar serviço", e);
        }
    }

    public List<Servico> findAll() {
        try {
            return servicoRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar todos os serviços: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar serviços", e);
        }
    }

    public boolean delete(Long id) {
        try {
            if (servicoRepository.existsById(id)) {
                servicoRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (DataAccessException e) {
            logger.error("Erro ao deletar serviço com ID: {}", id, e);
            throw new RuntimeException("Erro ao deletar serviço", e);
        }
    }

    public boolean existsByNome(String nome) {
        try {
            return servicoRepository.existsByNome(nome);
        } catch (DataAccessException e) {
            logger.error("Erro ao verificar existência do serviço por nome: {}", nome, e);
            throw new RuntimeException("Erro ao verificar existência do serviço", e);
        }
    }
}