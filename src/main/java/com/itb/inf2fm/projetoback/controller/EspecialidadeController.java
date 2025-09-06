// Controller REST para Especialidade
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /especialidade, /especialidade/{id} podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Especialidade;
import com.itb.inf2fm.projetoback.service.EspecialidadeService;
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

@Tag(name = "Especialidade", description = "API para gerenciamento de especialidades técnicas")
@RestController
@RequestMapping("/especialidade")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;
    
    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @Operation(summary = "Criar nova especialidade", description = "Cria uma nova especialidade no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Especialidade criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Especialidade.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<Especialidade> createEspecialidade(
            @Parameter(description = "Dados da especialidade a ser criada", required = true)
            @Valid @RequestBody Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            Especialidade saved = especialidadeService.save(especialidade);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Listar todas as especialidades", description = "Retorna uma lista com todas as especialidades cadastradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de especialidades retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Especialidade.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<Especialidade>> getAllEspecialidades() {
        try {
            List<Especialidade> especialidades = especialidadeService.findAll();
            return new ResponseEntity<>(especialidades, HttpStatus.OK);
        } catch (org.springframework.dao.DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Buscar especialidade por ID", description = "Retorna uma especialidade específica baseada no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especialidade encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Especialidade.class))),
        @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Especialidade> getEspecialidadeById(
            @Parameter(description = "ID da especialidade", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Especialidade especialidade = especialidadeService.findById(id);
            if (especialidade != null) {
                return new ResponseEntity<>(especialidade, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (org.springframework.dao.DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Buscar especialidade por nome", description = "Retorna uma especialidade específica baseada no nome fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especialidade encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Especialidade.class))),
        @ApiResponse(responseCode = "400", description = "Nome inválido fornecido"),
        @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Especialidade> getEspecialidadeByNome(
            @Parameter(description = "Nome da especialidade", required = true, example = "Cardiolgia")
            @PathVariable String nome) {
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
        } catch (org.springframework.dao.DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Atualizar especialidade", description = "Atualiza os dados de uma especialidade existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Especialidade.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Especialidade> updateEspecialidade(
            @Parameter(description = "ID da especialidade", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados da especialidade", required = true)
            @Valid @RequestBody Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        especialidade.setId(id);
        try {
            Especialidade updated = especialidadeService.save(especialidade);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Deletar especialidade", description = "Remove uma especialidade do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Especialidade deletada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidade(
            @Parameter(description = "ID da especialidade", required = true, example = "1")
            @PathVariable Long id) {
        try {
            especialidadeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (org.springframework.dao.DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}