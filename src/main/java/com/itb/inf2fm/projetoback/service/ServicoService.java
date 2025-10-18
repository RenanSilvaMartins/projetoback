package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.*;
import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import com.itb.inf2fm.projetoback.util.CrudValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        CrudValidationUtils.validateId(id, "Serviço");
        
        try {
            return CrudValidationUtils.validateResourceExists(
                () -> servicoRepository.findById(id).orElse(null),
                "Serviço", id
            );
        } catch (DataAccessException e) {
            throw new DatabaseException("buscar serviço", "Erro ao buscar serviço no banco de dados");
        }
    }

    public List<Servico> buscarPorNome(String nome) {
        return servicoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Servico> buscarPorTipo(String tipo) {
        return servicoRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Servico> buscarPorTermo(String termo) {
        return servicoRepository.buscarPorTermo(termo);
    }

    @Transactional
    public Servico salvar(Servico servico) {
        if (servico == null) {
            throw new ValidationException("Serviço não pode ser nulo");
        }
        
        // Validações de campos obrigatórios
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("nome", servico.getNome());
        requiredFields.put("tipo", servico.getTipo());
        requiredFields.put("preco", servico.getPreco());
        
        CrudValidationUtils.validateRequiredFields(requiredFields);
        
        try {
            return servicoRepository.save(servico);
        } catch (DataAccessException e) {
            throw new DatabaseException("salvar serviço", "Erro ao salvar serviço no banco de dados");
        }
    }

    @Transactional
    public Servico atualizar(Long id, Servico servicoAtualizado) {
        CrudValidationUtils.validateId(id, "Serviço");
        
        Servico servico = CrudValidationUtils.validateResourceExists(
            () -> servicoRepository.findById(id).orElse(null),
            "Serviço", id
        );
        
        try {
            if (servicoAtualizado.getNome() != null) {
                servico.setNome(servicoAtualizado.getNome());
            }
            if (servicoAtualizado.getDuracao() != null) {
                servico.setDuracao(servicoAtualizado.getDuracao());
            }
            if (servicoAtualizado.getPreco() != null) {
                servico.setPreco(servicoAtualizado.getPreco());
            }
            if (servicoAtualizado.getTipo() != null) {
                servico.setTipo(servicoAtualizado.getTipo());
            }
            
            return servicoRepository.save(servico);
        } catch (DataAccessException e) {
            throw new DatabaseException("atualizar serviço", "Erro ao atualizar serviço no banco de dados");
        }
    }

    @Transactional
    public void deletar(Long id) {
        CrudValidationUtils.validateId(id, "Serviço");
        
        CrudValidationUtils.validateResourceExists(
            () -> servicoRepository.existsById(id) ? "exists" : null,
            "Serviço", id
        );
        
        try {
            servicoRepository.deleteById(id);
        } catch (DataAccessException e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                throw new InvalidOperationException("deletar serviço", 
                    "Serviço possui agendamentos associados");
            }
            throw new DatabaseException("deletar serviço", "Erro ao deletar serviço do banco de dados");
        }
    }
}