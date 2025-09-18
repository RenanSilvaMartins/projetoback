package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByUsuarioId(Long usuarioId);
    List<Agendamento> findByTecnicoId(Long tecnicoId);
    List<Agendamento> findByDataAgendamento(LocalDate dataAgendamento);
}