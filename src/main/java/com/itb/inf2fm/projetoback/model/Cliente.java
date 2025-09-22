// Entidade Cliente
// Esta classe representa o modelo de dados do cliente no backend
// Os campos mapeados (@Column) são persistidos no banco e expostos via API REST
// Integração com Frontend: os dados retornados pelos endpoints podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize models equivalentes e pacotes http/dio para consumir endpoints
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints e popular componentes
// Campos como nome, email, cpf, dataNascimento, statusCliente são essenciais para cadastro e autenticação
package com.itb.inf2fm.projetoback.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "Cliente")
public class Cliente {

    private static final int CPF_LENGTH = 11;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", length = 11, nullable = false)
    private String cpf;

    @Column(name = "dataNascimento", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "statusCliente", length = 20, nullable = false)
    private String statusCliente;

    @Transient
    private String mensagemErro = "";

    @Transient
    private boolean isValid = true;

    public Cliente() {
    }

    public Cliente(String cpf, LocalDate dataNascimento, Usuario usuario, String statusCliente) {
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setUsuario(usuario);
        setStatusCliente(statusCliente);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Nome e email vêm do Usuario relacionado
    public String getNome() {
        return usuario != null ? usuario.getNome() : null;
    }

    public String getEmail() {
        return usuario != null ? usuario.getEmail() : null;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            this.mensagemErro = "CPF é obrigatório";
            this.isValid = false;
            return;
        }
        
        // Remove caracteres não numéricos
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        if (cpfLimpo.length() != CPF_LENGTH) {
            this.mensagemErro = "CPF deve conter " + CPF_LENGTH + " dígitos";
            this.isValid = false;
            return;
        }
        
        if (!isValidCpf(cpfLimpo)) {
            this.mensagemErro = "CPF inválido";
            this.isValid = false;
            return;
        }
        
        this.cpf = cpfLimpo;
        this.mensagemErro = "";
        this.isValid = true;
    }
    
    private boolean isValidCpf(String cpf) {
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calcula o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;
        
        // Calcula o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;
        
        // Verifica se os dígitos calculados conferem com os informados
        return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
               Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            this.mensagemErro = "Data de nascimento é obrigatória";
            this.isValid = false;
            return;
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            this.mensagemErro = "Data de nascimento não pode ser no futuro";
            this.isValid = false;
            return;
        }
        this.dataNascimento = dataNascimento;
        this.mensagemErro = "";
        this.isValid = true;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            this.mensagemErro = "Usuário é obrigatório";
            this.isValid = false;
            return;
        }
        this.usuario = usuario;
        this.mensagemErro = "";
        this.isValid = true;
    }

    public String getStatusCliente() {
        return statusCliente;
    }

    public void setStatusCliente(String statusCliente) {
        if (statusCliente == null || statusCliente.trim().isEmpty()) {
            this.mensagemErro = "Status do cliente é obrigatório";
            this.isValid = false;
            return;
        }
        this.statusCliente = statusCliente;
        this.mensagemErro = "";
        this.isValid = true;
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
