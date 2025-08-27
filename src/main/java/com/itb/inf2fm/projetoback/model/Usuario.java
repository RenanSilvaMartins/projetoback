package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario", indexes = {
    @Index(name = "idx_usuario_email", columnList = "email", unique = true),
    @Index(name = "idx_usuario_status", columnList = "statusUsuario")
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String nivelAcesso, String statusUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
        this.statusUsuario = statusUsuario;
        this.dataCadastro = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "senha", length = 255, nullable = false)
    private String senha;

    @Column(name = "nivelAcesso", length = 10)
    private String nivelAcesso; // ADMIN ou USER

    @Lob
    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "dataCadastro", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;

    @Column(name = "statusUsuario", length = 20, nullable = false)
    private String statusUsuario; // ATIVO ou INATIVO ou TROCAR_SENHA

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

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
        return senha; // Necessário para autenticação interna
    }
    
    public String getSenhaForResponse() {
        return null; // Para respostas da API
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

    public byte[] getFoto() {
        return foto != null ? foto.clone() : null;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
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

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean valid) {
        isValid = valid;
    }

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }
}
