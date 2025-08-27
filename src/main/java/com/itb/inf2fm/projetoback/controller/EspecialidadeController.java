// Controller REST para Especialidade
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /especialidade, /especialidade/{id} podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Especialidade;
import com.itb.inf2fm.projetoback.service.EspecialidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/especialidade")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @PostMapping
    public ResponseEntity<Especialidade> createEspecialidade(@RequestBody Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            Especialidade saved = especialidadeService.save(especialidade);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Especialidade>> getAllEspecialidades() {
        try {
            List<Especialidade> especialidades = especialidadeService.findAll();
            return new ResponseEntity<>(especialidades, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especialidade> getEspecialidadeById(@PathVariable Long id) {
        try {
            Especialidade especialidade = especialidadeService.findById(id);
            if (especialidade != null) {
                return new ResponseEntity<>(especialidade, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Especialidade> getEspecialidadeByNome(@PathVariable String nome) {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Especialidade especialidade = especialidadeService.findByNome(nome);
            if (especialidade != null) {
                return new ResponseEntity<>(especialidade, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Especialidade> updateEspecialidade(@PathVariable Long id, @RequestBody Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        especialidade.setId(id);
        try {
            Especialidade updated = especialidadeService.save(especialidade);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidade(@PathVariable Long id) {
        try {
            especialidadeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}