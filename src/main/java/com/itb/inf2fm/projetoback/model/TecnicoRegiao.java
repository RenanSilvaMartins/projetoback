package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TecnicoRegiao")
public class TecnicoRegiao {

    public TecnicoRegiao() {
    }

    public TecnicoRegiao(Tecnico tecnico, Regiao regiao, String statusTecnicoRegiao) {
        this.tecnico = tecnico;
        this.regiao = regiao;
        this.statusTecnicoRegiao = statusTecnicoRegiao;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    @Column(name = "statusTecnicoRegiao", length = 20, nullable = false)
    private String statusTecnicoRegiao;

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

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    public String getStatusTecnicoRegiao() {
        return statusTecnicoRegiao;
    }

    public void setStatusTecnicoRegiao(String statusTecnicoRegiao) {
        this.statusTecnicoRegiao = statusTecnicoRegiao;
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
