package com.itb.inf2fm.projetoback.exception;

/**
 * Exceção para erros de banco de dados
 */
public class DatabaseException extends RuntimeException {
    
    private final String operation;
    
    public DatabaseException(String message) {
        super(message);
        this.operation = null;
    }
    
    public DatabaseException(String operation, String message) {
        super(String.format("Erro na operação '%s': %s", operation, message));
        this.operation = operation;
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
    }
    
    public String getOperation() {
        return operation;
    }
}