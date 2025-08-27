package com.itb.inf2fm.projetoback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para resposta de autenticação
 */
@Schema(description = "Resposta da autenticação com token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID do usuário autenticado", example = "1")
    private Long userId;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String nome;

    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Nível de acesso", example = "USER")
    private String nivelAcesso;

    @Schema(description = "Tempo de expiração do token em segundos", example = "86400")
    private Long expiresIn;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String nome, String email, String nivelAcesso) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.nivelAcesso = nivelAcesso;
        this.expiresIn = 86400L; // 24 horas
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
