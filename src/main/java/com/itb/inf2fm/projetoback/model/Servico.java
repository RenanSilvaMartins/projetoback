package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private Tecnico tecnico;

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

    public Servico() {
    }

    public Servico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
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