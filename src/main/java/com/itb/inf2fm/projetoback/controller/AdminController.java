// Controller REST para Admin
// Expõe endpoints para operações administrativas e de gerenciamento
// Integração com Frontend: endpoints como /admin, /admin/{id} podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.service.PasswordEncryptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PasswordEncryptService passwordEncryptService;

    public AdminController(PasswordEncryptService passwordEncryptService) {
        this.passwordEncryptService = passwordEncryptService;
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
}