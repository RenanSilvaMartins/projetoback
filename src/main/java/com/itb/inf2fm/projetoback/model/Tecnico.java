package com.itb.inf2fm.projetoback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Tecnico")
public class Tecnico {

    public Tecnico() {
    }

    public Tecnico(String cpfCnpj, LocalDate dataNascimento, String telefone, String cep, 
                   String numeroResidencia, String complemento, String descricao, 
                   Usuario usuario, String statusTecnico) {
        this.cpfCnpj = cpfCnpj;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.cep = cep;
        this.numeroResidencia = numeroResidencia;
        this.complemento = complemento;
        this.descricao = descricao;
        this.usuario = usuario;
        this.statusTecnico = statusTecnico;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_cnpj", length = 14, nullable = false)
    private String cpfCnpj;

    @Column(name = "dataNascimento", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    @Column(name = "cep", length = 8, nullable = false)
    private String cep;

    @Column(name = "numeroResidencia", length = 10, nullable = false)
    private String numeroResidencia;

    @Column(name = "complemento", length = 10, nullable = false)
    private String complemento;

    @Column(name = "descricao", length = 400, nullable = false)
    private String descricao;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "statusTecnico", length = 20, nullable = false)
    private String statusTecnico;

    // Relacionamentos Many-to-Many
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TecnicoRegiao> tecnicoRegioes = new HashSet<>();

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TecnicoEspecialidade> tecnicoEspecialidades = new HashSet<>();

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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumeroResidencia() {
        return numeroResidencia;
    }

    public void setNumeroResidencia(String numeroResidencia) {
        this.numeroResidencia = numeroResidencia;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getStatusTecnico() {
        return statusTecnico;
    }

    public void setStatusTecnico(String statusTecnico) {
        this.statusTecnico = statusTecnico;
    }

    public Set<TecnicoRegiao> getTecnicoRegioes() {
        return new HashSet<>(tecnicoRegioes);
    }

    public void setTecnicoRegioes(Set<TecnicoRegiao> tecnicoRegioes) {
        this.tecnicoRegioes = tecnicoRegioes;
    }

    public Set<TecnicoEspecialidade> getTecnicoEspecialidades() {
        return new HashSet<>(tecnicoEspecialidades);
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
