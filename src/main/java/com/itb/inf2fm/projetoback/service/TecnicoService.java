package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.TecnicoRepository;
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

    public Tecnico findAllById(long id) {
        Tecnico tecnicoEncontrado = tecnicoRepository.findAllById(id);
        return tecnicoEncontrado;
    }

    @Transactional
    public Tecnico update(Tecnico _tecnico) {
        Tecnico tecnicoEncontrado = tecnicoRepository.findAllById(_tecnico.getId());
        if (tecnicoEncontrado.getId() > 0)
            return tecnicoRepository.save(tecnicoEncontrado);
        else
            return new Tecnico(0, "Produto não encontrado");
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
