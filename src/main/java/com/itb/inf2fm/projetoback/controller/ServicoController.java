package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico")
@Tag(name = "Serviços", description = "API para gerenciamento de serviços")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @GetMapping
    @Operation(summary = "Listar todos os serviços")
    public ResponseEntity<List<Servico>> listarTodos() {
        List<Servico> servicos = servicoService.listarTodos();
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        Servico servico = servicoService.buscarPorId(id);
        return ResponseEntity.ok(servico);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar serviços por termo")
    public ResponseEntity<List<Servico>> buscarPorTermo(@RequestParam String termo) {
        List<Servico> servicos = servicoService.buscarPorTermo(termo);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar serviços por nome")
    public ResponseEntity<List<Servico>> buscarPorNome(@PathVariable String nome) {
        List<Servico> servicos = servicoService.buscarPorNome(nome);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar serviços por tipo")
    public ResponseEntity<List<Servico>> buscarPorTipo(@PathVariable String tipo) {
        List<Servico> servicos = servicoService.buscarPorTipo(tipo);
        return ResponseEntity.ok(servicos);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo serviço")
    public ResponseEntity<Servico> cadastrar(@Valid @RequestBody Servico servico) {
        Servico novoServico = servicoService.salvar(servico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @Valid @RequestBody Servico servico) {
        Servico servicoAtualizado = servicoService.atualizar(id, servico);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}