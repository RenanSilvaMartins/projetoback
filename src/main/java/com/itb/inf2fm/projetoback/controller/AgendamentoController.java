package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.dto.AgendamentoRequest;
import com.itb.inf2fm.projetoback.model.Agendamento;
import com.itb.inf2fm.projetoback.service.AgendamentoService;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;

import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    @Autowired  
    private AgendamentoService agendamentoService;

    @Autowired
    private TecnicoRepository tecnicoRepository;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Agendamento> findAll() {
        return agendamentoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> findById(@PathVariable Long id) {
        return agendamentoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Agendamento> create(@RequestBody AgendamentoRequest request) {
        return ResponseEntity.ok(agendamentoService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> update(@PathVariable Long id, @RequestBody AgendamentoRequest request) {
        if (!agendamentoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agendamentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!agendamentoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        agendamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Agendamento> findByUsuarioId(@PathVariable Long usuarioId) {
        return agendamentoService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<Agendamento> findByTecnicoId(@PathVariable Long tecnicoId) {
        return agendamentoService.findByTecnicoId(tecnicoId);
    }

    @GetMapping("/data/{dataAgendamento}")
    public List<Agendamento> findByDataAgendamento(@PathVariable LocalDate dataAgendamento) {
        return agendamentoService.findByDataAgendamento(dataAgendamento);
    }
}