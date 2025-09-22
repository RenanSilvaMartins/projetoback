package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "horaAgendamento")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate horaAgendamento;

    @Column(name = "dataAgendamento")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAgendamento;

    @Column(name = "tecnico_id")
    private Long tecnicoId;

    @Column(name = "especialidade_id")
    private Long especialidadeId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "descricao", length = 200)
    private String descricao;

    @Column(name = "urgencia", length = 200)
    private String urgencia;

    @Column(name = "situacao", length = 200)
    private String situacao;

    @Column(name = "preco", precision = 15, scale = 2)
    private BigDecimal preco;

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

    public Agendamento() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getHoraAgendamento() { return horaAgendamento; }
    public void setHoraAgendamento(LocalDate horaAgendamento) { this.horaAgendamento = horaAgendamento; }

    public LocalDate getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDate dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public Long getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Long especialidadeId) { this.especialidadeId = especialidadeId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUrgencia() { return urgencia; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getMensagemErro() { return mensagemErro; }
    public void setMensagemErro(String mensagemErro) { this.mensagemErro = mensagemErro; }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
}