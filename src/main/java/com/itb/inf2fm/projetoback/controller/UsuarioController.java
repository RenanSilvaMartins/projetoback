// Controller REST para Usuário
// Expõe endpoints para autenticação, cadastro e gerenciamento de usuários
// Integração com Frontend: endpoints como /usuario/authenticate, /usuario podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.service.UsuarioService;
import com.itb.inf2fm.projetoback.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Usuários", description = "API para gerenciamento de usuários do sistema")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(
            @Parameter(description = "Dados do usuário a ser criado") 
            @Valid @RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários do sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico baseado no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(
            @Parameter(description = "ID do usuário") 
            @PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @Parameter(description = "ID do usuário") 
            @PathVariable Long id, 
            @Parameter(description = "Novos dados do usuário") 
            @Valid @RequestBody Usuario usuario) {
        usuario.setId(id);
        Usuario updated = usuarioService.save(usuario);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID do usuário") 
            @PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Autenticar usuário (Login)", description = "Realiza a autenticação de um usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(
            @Parameter(description = "Credenciais de login") 
            @Valid @RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.authenticate(loginRequest.getEmail(), loginRequest.getSenha());
        if (usuario != null && usuario.isValid()) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Autenticar usuário (Authenticate)", description = "Endpoint alternativo para autenticação de usuário")
    @PostMapping("/authenticate")
    public ResponseEntity<Usuario> authenticate(
            @Parameter(description = "Credenciais de autenticação") 
            @Valid @RequestBody LoginRequest loginRequest) {
        return login(loginRequest); // Delega para o método login existente
    }
    
    @Operation(summary = "Buscar usuários por nome", description = "Retorna uma lista de usuários que contenham o nome fornecido")
    @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados")
    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorNome(
            @Parameter(description = "Nome ou parte do nome do usuário") 
            @PathVariable String nome) {
        List<Usuario> usuarios = usuarioService.findByNome(nome);
        return ResponseEntity.ok(usuarios);
    }
}