// Controller REST para Admin
// Expõe endpoints para operações administrativas e de gerenciamento
// Integração com Frontend: endpoints como /admin, /admin/{id} podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.service.PasswordEncryptService;
import com.itb.inf2fm.projetoback.service.ServicoService;
import com.itb.inf2fm.projetoback.service.TecnicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "API para operações administrativas")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PasswordEncryptService passwordEncryptService;
    private final ServicoService servicoService;
    private final TecnicoService tecnicoService;

    public AdminController(PasswordEncryptService passwordEncryptService, ServicoService servicoService, TecnicoService tecnicoService) {
        this.passwordEncryptService = passwordEncryptService;
        this.servicoService = servicoService;
        this.tecnicoService = tecnicoService;
    }

    @PostMapping("/encrypt-passwords")
    public ResponseEntity<String> encryptExistingPasswords() {
        try {
            passwordEncryptService.encryptExistingPasswords();
            return ResponseEntity.ok("Senhas criptografadas com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criptografar senhas");
        }
    }

    @Operation(summary = "Criar novo serviço", description = "Permite ao admin criar um novo serviço no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/servicos")
    public ResponseEntity<Servico> createServico(
            @Parameter(description = "Dados do serviço a ser criado", required = true)
            @Valid @RequestBody Servico servico) {
        try {
            Servico saved = servicoService.salvar(servico);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista com todos os serviços cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/servicos")
    public ResponseEntity<List<Servico>> getAllServicos() {
        try {
            List<Servico> servicos = servicoService.listarTodos();
            return new ResponseEntity<>(servicos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Deletar serviço", description = "Permite ao admin remover um serviço do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/servicos/{id}")
    public ResponseEntity<Void> deleteServico(
            @Parameter(description = "ID do serviço", required = true, example = "1")
            @PathVariable Long id) {
        try {
            servicoService.deletar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cadastrar novo técnico", description = "Permite ao admin cadastrar um novo técnico no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Técnico cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/tecnicos")
    public ResponseEntity<Object> createTecnico(
            @Parameter(description = "Dados do técnico a ser cadastrado", required = true)
            @Valid @RequestBody Tecnico tecnico) {
        try {
            if (tecnico == null || tecnico.getUsuario() == null) {
                return ResponseEntity.badRequest().body("Dados do técnico e usuário são obrigatórios");
            }
            
            Tecnico saved = tecnicoService.salvarTecnico(tecnico);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Erro interno do servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Listar todos os técnicos", description = "Retorna uma lista com todos os técnicos cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de técnicos retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/tecnicos")
    public ResponseEntity<List<Tecnico>> getAllTecnicos() {
        try {
            List<Tecnico> tecnicos = tecnicoService.findAll();
            return new ResponseEntity<>(tecnicos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Remover técnico", description = "Permite ao admin remover um técnico do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Técnico removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Técnico não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/tecnicos/{id}")
    public ResponseEntity<Object> deleteTecnico(
            @Parameter(description = "ID do técnico", required = true, example = "1")
            @PathVariable Long id) {
        try {
            if (id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            
            tecnicoService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Erro interno do servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Inativar técnico", description = "Permite ao admin inativar um técnico sem removê-lo do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Técnico inativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Técnico não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/tecnicos/{id}/inativar")
    public ResponseEntity<Object> inativarTecnico(
            @Parameter(description = "ID do técnico", required = true, example = "1")
            @PathVariable Long id) {
        try {
            if (id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            
            Tecnico tecnicoInativado = tecnicoService.inativar(id);
            return ResponseEntity.ok(tecnicoInativado);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Erro interno do servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}