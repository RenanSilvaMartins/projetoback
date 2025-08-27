// Controller REST para Técnico
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /tecnico, /tecnico/{id}, /tecnico?email= podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.service.PasswordEncryptService;
import com.itb.inf2fm.projetoback.service.TecnicoService;
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
import java.util.Optional;

/**
 * TECNICO CONTROLLER - API REST para gerenciamento de técnicos
 * Base URL: http://localhost:8082/tecnico
 * 
 * IMPORTANTE: Técnicos possuem relacionamento com Usuário, Especialidades e Regiões
 * 
 * Estrutura do Técnico:
 * {
 *   "id": 1,
 *   "cpfCnpj": "12345678901",
 *   "dataNascimento": "1990-01-01",
 *   "telefone": "11999999999",
 *   "cep": "01234567",
 *   "numeroResidencia": "123",
 *   "complemento": "Apto 45",
 *   "descricao": "Técnico especializado em hardware",
 *   "statusTecnico": "ATIVO",
 *   "usuario": { ... }
 * }
 */
@RestController
@RequestMapping("/tecnico")
public class TecnicoController {

        private final TecnicoService tecnicoService;

        public TecnicoController(TecnicoService tecnicoService, PasswordEncryptService passwordEncryptService) {
            this.tecnicoService = tecnicoService;
        }

        @PostMapping
        public ResponseEntity<Object> saveTecnico(@RequestBody Tecnico tecnico){
            if (tecnico == null || tecnico.getUsuario() == null) {
                return ResponseEntity.badRequest().body("Dados do técnico são obrigatórios");
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(tecnicoService.save(tecnico));
        }

        @GetMapping("/tecnico/{id}")
        public ResponseEntity<Tecnico> getTecnicoById(@PathVariable Long id) {
            if (id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Optional<Tecnico> tecnicoOpt = tecnicoService.findById(id);
            if (tecnicoOpt.isPresent()) {
                return ResponseEntity.ok(tecnicoOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        // ROTA GET
        @GetMapping
        public ResponseEntity<List<Tecnico>> getAllTecnicos(){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(tecnicoService.findAll());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Object> updateTecnico( @PathVariable Long id, @RequestBody Tecnico tecnico){
            if (id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            if (tecnico == null || tecnico.getUsuario() == null) {
                return ResponseEntity.badRequest().body("Dados do técnico são obrigatórios");
            }
            try {
                tecnico.setId(id);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(tecnicoService.update(tecnico));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro interno do servidor");
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Object> deleteTecnico(@PathVariable Long id){
            if (id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            boolean deleted = tecnicoService.delete(id);
            if (deleted) {
                return ResponseEntity.ok("Técnico deletado com sucesso");
            } else {
                return ResponseEntity.notFound().build();
            }
        }
}
