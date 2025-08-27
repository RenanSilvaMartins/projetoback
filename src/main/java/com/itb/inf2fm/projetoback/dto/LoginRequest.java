package com.itb.inf2fm.projetoback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    private String cpf;  // Para cliente

    private String cnpj; // Para técnico

    @Email(message = "Email deve ter formato válido")
    private String email; // Para login por email

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    // Construtores
    public LoginRequest() {}
    
    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
