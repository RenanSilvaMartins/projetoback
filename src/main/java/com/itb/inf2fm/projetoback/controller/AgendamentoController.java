package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.dto.AgendamentoRequest;
import com.itb.inf2fm.projetoback.dto.AgendamentoResponse;
import com.itb.inf2fm.projetoback.model.Agendamento;
import com.itb.inf2fm.projetoback.service.AgendamentoService;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
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
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<AgendamentoResponse> findAll() {
        return agendamentoService.findAll().stream()
                .map(AgendamentoResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> findById(@PathVariable Long id) {
        return agendamentoService.findById(id)
                .map(agendamento -> ResponseEntity.ok(new AgendamentoResponse(agendamento)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> create(@RequestBody AgendamentoRequest request) {
        Agendamento agendamento = agendamentoService.save(request);
        return ResponseEntity.ok(new AgendamentoResponse(agendamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> update(@PathVariable Long id, @RequestBody AgendamentoRequest request) {
        if (!agendamentoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Agendamento agendamento = agendamentoService.update(id, request);
        return ResponseEntity.ok(new AgendamentoResponse(agendamento));
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
    public List<AgendamentoResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return agendamentoService.findByUsuarioId(usuarioId).stream()
                .map(AgendamentoResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<AgendamentoResponse> findByTecnicoId(@PathVariable Long tecnicoId) {
        return agendamentoService.findByTecnicoId(tecnicoId).stream()
                .map(AgendamentoResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/data/{dataAgendamento}")
    public List<AgendamentoResponse> findByDataAgendamento(@PathVariable LocalDate dataAgendamento) {
        return agendamentoService.findByDataAgendamento(dataAgendamento).stream()
                .map(AgendamentoResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Endpoints para facilitar o cadastro de agendamentos
    
    @GetMapping("/tecnicos-disponiveis")
    public List<Tecnico> getTecnicosDisponiveis(@RequestParam(required = false) Long servicoId) {
        if (servicoId != null) {
            return agendamentoService.findTecnicosByServicoId(servicoId);
        }
        return tecnicoRepository.findByStatusTecnico("ATIVO");
    }
    
    @GetMapping("/usuarios-disponiveis")
    public List<Usuario> getUsuariosDisponiveis() {
        return usuarioRepository.findAll();
    }
    
    @GetMapping("/servicos-disponiveis")
    public List<Servico> getServicosDisponiveis() {
        return servicoRepository.findAll();
    }
    
    @GetMapping("/clientes-disponiveis")
    public List<Cliente> getClientesDisponiveis() {
        return clienteRepository.findAll();
    }
    
    @GetMapping("/tecnicos-por-servico/{servicoId}")
    public ResponseEntity<List<Tecnico>> getTecnicosPorServico(@PathVariable Long servicoId) {
        try {
            List<Tecnico> tecnicos = agendamentoService.findTecnicosByServicoId(servicoId);
            return ResponseEntity.ok(tecnicos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}