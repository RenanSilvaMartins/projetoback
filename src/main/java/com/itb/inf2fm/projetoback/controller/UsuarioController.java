// Controller REST para Usuário
// Expõe endpoints para autenticação, cadastro e gerenciamento de usuários
// Integração com Frontend: endpoints como /usuario/authenticate, /usuario podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.service.UsuarioService;
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
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            Usuario saved = usuarioService.save(usuario);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.findAll();
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario != null) {
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        usuario.setId(id);
        try {
            Usuario updated = usuarioService.save(usuario);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getSenha() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            Usuario usuario = usuarioService.authenticate(loginRequest.getEmail(), loginRequest.getSenha());
            if (usuario != null && usuario.isValid()) {
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}