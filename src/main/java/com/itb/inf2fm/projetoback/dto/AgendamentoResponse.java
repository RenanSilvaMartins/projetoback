package com.itb.inf2fm.projetoback.dto;

import com.itb.inf2fm.projetoback.model.Agendamento;
import java.time.LocalDate;

public class AgendamentoResponse {
    private Long id;
    private String horaAgendamento;
    private LocalDate dataAgendamento;
    private String descricao;
    private String urgencia;
    private String situacao;
    private double preco;
    private String servicoTipo;
    private String tecnicoNome;
    private String clienteNome;

    public AgendamentoResponse(Agendamento agendamento) {
        this.id = agendamento.getId();
        this.horaAgendamento = agendamento.getHoraAgendamento();
        this.dataAgendamento = agendamento.getDataAgendamento();
        this.descricao = agendamento.getDescricao();
        this.urgencia = agendamento.getUrgencia();
        this.situacao = agendamento.getSituacao();
        this.preco = agendamento.getPreco();
        this.servicoTipo = agendamento.getServico() != null ? agendamento.getServico().getTipo() : null;
        this.tecnicoNome = agendamento.getTecnico() != null && agendamento.getTecnico().getUsuario() != null 
            ? agendamento.getTecnico().getUsuario().getNome() : null;
        this.clienteNome = agendamento.getCliente() != null && agendamento.getCliente().getUsuario() != null 
            ? agendamento.getCliente().getUsuario().getNome() : null;
    }

    // Getters
    public Long getId() { return id; }
    public String getHoraAgendamento() { return horaAgendamento; }
    public LocalDate getDataAgendamento() { return dataAgendamento; }
    public String getDescricao() { return descricao; }
    public String getUrgencia() { return urgencia; }
    public String getSituacao() { return situacao; }
    public double getPreco() { return preco; }
    public String getServicoTipo() { return servicoTipo; }
    public String getTecnicoNome() { return tecnicoNome; }
    public String getClienteNome() { return clienteNome; }
}