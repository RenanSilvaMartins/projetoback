package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.dto.AgendamentoRequest;
import com.itb.inf2fm.projetoback.model.Agendamento;
import com.itb.inf2fm.projetoback.service.AgendamentoService;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.EspecialidadeRepository;
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
    private EspecialidadeRepository especialidadeRepository;

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
        Agendamento agendamento = new Agendamento();
        agendamento.setDataAgendamento(LocalDate.parse(request.getDataAgendamento().substring(0, 10)));
        agendamento.setHoraAgendamento(LocalTime.parse(request.getHorario()));
        agendamento.setDescricao(request.getDescricao());
        agendamento.setUrgencia(request.getUrgencia());
        agendamento.setSituacao(request.getStatus());
        agendamento.setPreco(request.getPreco());
        
        agendamento.setTecnico(tecnicoRepository.findById(request.getTecnicoId()).orElse(null));
        agendamento.setUsuario(usuarioRepository.findById(request.getUsuarioId()).orElse(null));
        
        return ResponseEntity.ok(agendamentoService.save(agendamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> update(@PathVariable Long id, @RequestBody AgendamentoRequest request) {
        if (!agendamentoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setDataAgendamento(LocalDate.parse(request.getDataAgendamento().substring(0, 10)));
        agendamento.setHoraAgendamento(LocalTime.parse(request.getHorario()));
        agendamento.setDescricao(request.getDescricao());
        agendamento.setUrgencia(request.getUrgencia());
        agendamento.setSituacao(request.getStatus());
        agendamento.setPreco(request.getPreco());
        
        agendamento.setTecnico(tecnicoRepository.findById(request.getTecnicoId()).orElse(null));
        agendamento.setUsuario(usuarioRepository.findById(request.getUsuarioId()).orElse(null));
        
        return ResponseEntity.ok(agendamentoService.save(agendamento));
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