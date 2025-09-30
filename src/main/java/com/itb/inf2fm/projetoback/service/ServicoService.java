package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exception.ResourceNotFoundException;
import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));
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

    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    public Servico atualizar(Long id, Servico servicoAtualizado) {
        Servico servico = buscarPorId(id);
        servico.setNome(servicoAtualizado.getNome());
        servico.setDuracao(servicoAtualizado.getDuracao());
        servico.setPreco(servicoAtualizado.getPreco());
        servico.setTipo(servicoAtualizado.getTipo());
        return servicoRepository.save(servico);
    }

    public void deletar(Long id) {
        Servico servico = buscarPorId(id);
        servicoRepository.delete(servico);
    }
}