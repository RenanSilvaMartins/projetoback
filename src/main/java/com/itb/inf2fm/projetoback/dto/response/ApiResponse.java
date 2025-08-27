package com.itb.inf2fm.projetoback.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Classe padrão para respostas da API
 * Fornece estrutura consistente para todas as respostas HTTP
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta padrão da API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private boolean success;

    @Schema(description = "Mensagem descritiva do resultado", example = "Operação realizada com sucesso")
    private String message;

    @Schema(description = "Dados retornados pela operação")
    private T data;

    @Schema(description = "Detalhes do erro (apenas em caso de falha)")
    private String error;

    @Schema(description = "Timestamp da resposta")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Path da requisição", example = "/api/usuarios")
    private String path;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, T data) {
        this(success, message);
        this.data = data;
    }

    public ApiResponse(boolean success, String message, String error) {
        this(success, message);
        this.error = error;
    }

    // Factory methods para criação simplificada
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, error);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
