package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Regiao")
public class Regiao {

    public Regiao() {
    }

    public Regiao(String cidade, String nome, String descricao, String statusRegiao) {
        this.cidade = cidade;
        this.nome = nome;
        this.descricao = descricao;
        this.statusRegiao = statusRegiao;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cidade", length = 100, nullable = false)
    private String cidade;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 200, nullable = false)
    private String descricao;

    @Column(name = "statusRegiao", length = 20, nullable = false)
    private String statusRegiao;

    @OneToMany(mappedBy = "regiao", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<TecnicoRegiao> tecnicoRegioes = new HashSet<>();

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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatusRegiao() {
        return statusRegiao;
    }

    public void setStatusRegiao(String statusRegiao) {
        this.statusRegiao = statusRegiao;
    }

    public Set<TecnicoRegiao> getTecnicoRegioes() {
        return tecnicoRegioes;
    }

    public void setTecnicoRegioes(Set<TecnicoRegiao> tecnicoRegioes) {
        this.tecnicoRegioes = tecnicoRegioes;
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

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
