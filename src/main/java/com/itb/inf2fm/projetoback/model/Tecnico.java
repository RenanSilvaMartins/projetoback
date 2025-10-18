package com.itb.inf2fm.projetoback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Tecnico")
public class Tecnico {

    @Id
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

    @Column(name = "especialidade", length = 100, nullable = false)
    private String especialidade;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "statusTecnico", length = 20, nullable = false)
    private String statusTecnico;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "TecnicoRegiao",
            joinColumns = @JoinColumn(name = "tecnico_id"),
            inverseJoinColumns = @JoinColumn(name = "regiao_id")
    )
    private List<Regiao> regioes;

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

    public Tecnico() {
    }

    public Tecnico(String cpfCnpj, LocalDate dataNascimento, String telefone, String cep,
            String numeroResidencia, String complemento, String descricao, String especialidade,
            Usuario usuario, String statusTecnico) {
        this.cpfCnpj = cpfCnpj;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.cep = cep;
        this.numeroResidencia = numeroResidencia;
        this.complemento = complemento;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.usuario = usuario;
        this.statusTecnico = statusTecnico;
    }

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

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
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

    public List<Regiao> getRegioes() {
        return regioes;
    }

    public void setRegioes(List<Regiao> regioes) {
        this.regioes = regioes;
    }

}
