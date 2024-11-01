package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.exceptions.NotFound;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoService {

    final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository _tecnicoRepository) {
        this.tecnicoRepository = _tecnicoRepository;
    }


    @Transactional
    public Tecnico save(Tecnico _tecnico) {
        return tecnicoRepository.save(_tecnico);
    }


    public List<Tecnico> findAll() {
        List<Tecnico> lista = tecnicoRepository.findAll();
        return lista;
    }

    public Tecnico findById(long id) {
        return tecnicoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Tecnico update(Tecnico tecnico) {
        Tecnico existingTecnico = tecnicoRepository.findById(tecnico.getId())
                .orElseThrow(() -> new NotFound("Tecnico não encontrado"));
        existingTecnico.setNome(tecnico.getNome());
        existingTecnico.setEmail(tecnico.getEmail());
        existingTecnico.setSenha(tecnico.getSenha());
        existingTecnico.setTelefone(tecnico.getTelefone());
        existingTecnico.setCnpj(tecnico.getCnpj());
        existingTecnico.setEspecialidade(tecnico.getEspecialidade());
        existingTecnico.setDataNascimento(tecnico.getDataNascimento());
        existingTecnico.setEstado(tecnico.getEstado());
        existingTecnico.setCidade(tecnico.getCidade());
        existingTecnico.setBairro(tecnico.getBairro());
        existingTecnico.setRua(tecnico.getRua());
        existingTecnico.setCasa(tecnico.getCasa());
        existingTecnico.setComplemento(tecnico.getComplemento());
        return tecnicoRepository.save(existingTecnico);
    }

    @Transactional
    public boolean delete(Tecnico _tecnico) {
        boolean sucesso = false;
        Tecnico tecnicoEncontrado = tecnicoRepository.findAllById(_tecnico.getId());
        if (tecnicoEncontrado.getId() > 0) {
            tecnicoRepository.deleteById(tecnicoEncontrado.getId());
            sucesso = true;
        }

        return sucesso;
    }
}
