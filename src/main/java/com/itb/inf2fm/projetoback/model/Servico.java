package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "Servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @NotBlank(message = "Duração é obrigatória")
    @Column(name = "duracao", length = 50, nullable = false)
    private String duracao;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    @Column(name = "preco", precision = 8, scale = 2, nullable = false)
    private BigDecimal preco;

    @NotBlank(message = "Tipo é obrigatório")
    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    public Servico() {}

    public Servico(String nome, String duracao, BigDecimal preco, String tipo) {
        this.nome = nome;
        this.duracao = duracao;
        this.preco = preco;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDuracao() { return duracao; }
    public void setDuracao(String duracao) { this.duracao = duracao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}