package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.model.Agendamento;
import com.itb.inf2fm.projetoback.repository.AgendamentoRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.EspecialidadeRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    public Agendamento save(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public void deleteById(Long id) {
        agendamentoRepository.deleteById(id);
    }

    public List<Agendamento> findByUsuarioId(Long usuarioId) {
        return agendamentoRepository.findByUsuarioId(usuarioId);
    }

    public List<Agendamento> findByTecnicoId(Long tecnicoId) {
        return agendamentoRepository.findByTecnicoId(tecnicoId);
    }

    public List<Agendamento> findByDataAgendamento(LocalDate dataAgendamento) {
        return agendamentoRepository.findByDataAgendamento(dataAgendamento);
    }
}