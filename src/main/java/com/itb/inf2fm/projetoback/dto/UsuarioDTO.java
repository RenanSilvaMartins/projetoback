package com.itb.inf2fm.projetoback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Base64;

/**
 * DTO para transferência de dados de usuário, evitando problemas de serialização
 */
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private String nivelAcesso;

    private String foto; // String para Base64

    private LocalDateTime dataCadastro;

    private String statusUsuario;

    // Constructors
    public UsuarioDTO() {}

    public UsuarioDTO(String nome, String email, String senha, String nivelAcesso, String statusUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
        this.statusUsuario = statusUsuario;
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public byte[] getFotoBytes() {
        if (foto == null || foto.trim().isEmpty()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(foto);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setFotoFromBytes(byte[] fotoBytes) {
        if (fotoBytes == null || fotoBytes.length == 0) {
            this.foto = null;
        } else {
            this.foto = Base64.getEncoder().encodeToString(fotoBytes);
        }
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(String statusUsuario) {
        this.statusUsuario = statusUsuario;
    }
}
