package com.itb.inf2fm.projetoback.exception;

/**
 * Exceção para operações inválidas (ex: deletar recurso com dependências)
 */
public class InvalidOperationException extends RuntimeException {
    
    private final String operation;
    private final String reason;
    
    public InvalidOperationException(String message) {
        super(message);
        this.operation = null;
        this.reason = null;
    }
    
    public InvalidOperationException(String operation, String reason) {
        super(String.format("Operação '%s' não permitida: %s", operation, reason));
        this.operation = operation;
        this.reason = reason;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getReason() {
        return reason;
    }
}