package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.dto.AgendamentoRequest;
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
    
    // Endpoints para facilitar o cadastro de agendamentos
    
    @GetMapping("/tecnicos-disponiveis")
    public List<Tecnico> getTecnicosDisponiveis() {
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
}