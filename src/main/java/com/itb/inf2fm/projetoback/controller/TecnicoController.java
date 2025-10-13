// Controller REST para Técnico
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /tecnico, /tecnico/{id}, /tecnico?email= podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.TecnicoRegiao;
import com.itb.inf2fm.projetoback.service.PasswordEncryptService;
import com.itb.inf2fm.projetoback.service.TecnicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Tecnico", description = "API para gerenciamento de técnicos e seus dados relacionados")
@RestController
@RequestMapping("/tecnico")
public class TecnicoController {

        private final TecnicoService tecnicoService;

        public TecnicoController(TecnicoService tecnicoService, PasswordEncryptService passwordEncryptService) {
            this.tecnicoService = tecnicoService;
        }

        @Operation(summary = "Criar novo técnico", description = "Cria um novo técnico no sistema com todos os dados relacionados")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Técnico criado com sucesso",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tecnico.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping
        public ResponseEntity<Object> saveTecnico(
                @Parameter(description = "Dados do técnico a ser criado", required = true)
                @Valid @RequestBody Tecnico tecnico){
            if (tecnico == null || tecnico.getUsuario() == null) {
                return ResponseEntity.badRequest().body("Dados do técnico são obrigatórios");
            }
            if (tecnico.getUsuario().getSenha() == null || tecnico.getUsuario().getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Senha do usuário é obrigatória");
            }
            try {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(tecnicoService.salvarTecnico(tecnico));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @Operation(summary = "Buscar técnico por ID", description = "Retorna um técnico específico baseado no ID fornecido")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Técnico encontrado",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tecnico.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido fornecido"),
            @ApiResponse(responseCode = "404", description = "Técnico não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Tecnico> getTecnicoById(
                @Parameter(description = "ID do técnico", required = true, example = "1")
                @PathVariable Long id) {
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
        
        @Operation(summary = "Buscar técnicos por nome", description = "Retorna uma lista de técnicos que contenham o nome fornecido")
        @ApiResponse(responseCode = "200", description = "Lista de técnicos encontrados")
        @GetMapping("/buscar/{nome}")
        public ResponseEntity<List<Tecnico>> buscarTecnicosPorNome(
                @Parameter(description = "Nome ou parte do nome do técnico") 
                @PathVariable String nome) {
            List<Tecnico> tecnicos = tecnicoService.findByNome(nome);
            return ResponseEntity.ok(tecnicos);
        }
        
        @GetMapping("/especialidades")
        public List<String> getEspecialidades() {
            return tecnicoService.getEspecialidades();
        }
        
        @GetMapping("/{id}/regioes")
        public ResponseEntity<List<TecnicoRegiao>> getRegioesTecnico(@PathVariable Long id) {
            try {
                return ResponseEntity.ok(tecnicoService.getRegioesByTecnico(id));
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
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
