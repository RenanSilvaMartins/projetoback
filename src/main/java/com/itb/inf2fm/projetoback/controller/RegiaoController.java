// Controller REST para Região
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /regiao, /regiao/{id} podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Regiao;
import com.itb.inf2fm.projetoback.service.RegiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REGIAO CONTROLLER - API REST para gerenciamento de regiões
 * Base URL: http://localhost:8082/regiao
 * 
 * Endpoints principais:
 * GET /regiao - Listar todas
 * GET /regiao/ativas - Listar apenas ativas (para dropdowns)
 * GET /regiao/{id} - Buscar por ID
 * GET /regiao/cidade/{cidade} - Buscar por cidade
 * POST /regiao - Criar nova
 * PUT /regiao/{id} - Atualizar
 * DELETE /regiao/{id} - Deletar
 */
@RestController
@RequestMapping("/regiao")
public class RegiaoController {

    @Autowired
    private RegiaoService regiaoService;

    @PostMapping
    public ResponseEntity<Regiao> createRegiao(@RequestBody Regiao regiao) {
        Regiao regiaoSalva = regiaoService.save(regiao);
        return new ResponseEntity<>(regiaoSalva, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Regiao>> getAllRegioes() {
        List<Regiao> regioes = regiaoService.findAll();
        return new ResponseEntity<>(regioes, HttpStatus.OK);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Regiao>> getRegioesAtivas() {
        List<Regiao> regioes = regiaoService.findByStatus("ATIVO");
        return new ResponseEntity<>(regioes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regiao> getRegiaoById(@PathVariable Long id) {
        Regiao regiao = regiaoService.findById(id);
        return new ResponseEntity<>(regiao, HttpStatus.OK);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Regiao> getRegiaoByNome(@PathVariable String nome) {
        Regiao regiao = regiaoService.findByNome(nome);
        
        if (regiao != null && regiao.isValid()) {
            return new ResponseEntity<>(regiao, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<Regiao>> getRegioesByCidade(@PathVariable String cidade) {
        List<Regiao> regioes = regiaoService.findByCidade(cidade);
        return new ResponseEntity<>(regioes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Regiao> updateRegiao(@PathVariable Long id, @RequestBody Regiao regiao) {
        regiao.setId(id);
        Regiao regiaoAtualizada = regiaoService.save(regiao);
        return new ResponseEntity<>(regiaoAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegiao(@PathVariable Long id) {
        regiaoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultRegioes() {
        try {
            regiaoService.initializeDefaultRegioes();
            return new ResponseEntity<>("Regiões padrão inicializadas com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao inicializar regiões", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
