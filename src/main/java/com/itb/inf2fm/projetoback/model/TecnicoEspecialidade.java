package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TecnicoEspecialidade")
public class TecnicoEspecialidade {

    public TecnicoEspecialidade() {
    }

    public TecnicoEspecialidade(Tecnico tecnico, Especialidade especialidade, String statusTecnicoEspecialidade) {
        this.tecnico = tecnico;
        this.especialidade = especialidade;
        this.statusTecnicoEspecialidade = statusTecnicoEspecialidade;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = false)
    private Especialidade especialidade;

    @Column(name = "statusTecnicoEspecialidade", length = 20, nullable = false)
    private String statusTecnicoEspecialidade;

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

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public String getStatusTecnicoEspecialidade() {
        return statusTecnicoEspecialidade;
    }

    public void setStatusTecnicoEspecialidade(String statusTecnicoEspecialidade) {
        this.statusTecnicoEspecialidade = statusTecnicoEspecialidade;
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
