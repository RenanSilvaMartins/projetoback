package com.itb.inf2fm.projetoback.dto;

public class LoginResponse {

    private Long id;
    private String tipo;
    private String token;
    private String nome;

    public LoginResponse(Long id, String tipo, String token) {
        this.id = id;
        this.tipo = tipo;
        this.token = token;
    }

    public LoginResponse(Long id, String tipo, String token, String nome) {
        this.id = id;
        this.tipo = tipo;
        this.token = token;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getToken() {
        return token;
    }

    public String getNome() {
        return nome;
    }


}
