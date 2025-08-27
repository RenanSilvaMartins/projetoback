package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Especialidade")
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 200, nullable = false)
    private String descricao;

    @Column(name = "statusEspecialidade", length = 20, nullable = false)
    private String statusEspecialidade;

    @OneToMany(mappedBy = "especialidade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TecnicoEspecialidade> tecnicoEspecialidades = new HashSet<>();

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

    public Especialidade() {
    }

    public Especialidade(String nome, String descricao, String statusEspecialidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.statusEspecialidade = statusEspecialidade;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatusEspecialidade() {
        return statusEspecialidade;
    }

    public void setStatusEspecialidade(String statusEspecialidade) {
        this.statusEspecialidade = statusEspecialidade;
    }

    public Set<TecnicoEspecialidade> getTecnicoEspecialidades() {
        return tecnicoEspecialidades;
    }

    public void setTecnicoEspecialidades(Set<TecnicoEspecialidade> tecnicoEspecialidades) {
        this.tecnicoEspecialidades = tecnicoEspecialidades;
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
