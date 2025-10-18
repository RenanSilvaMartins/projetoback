package com.itb.inf2fm.projetoback.exception;

/**
 * Exceção para recursos duplicados (CPF, email, etc.)
 */
public class DuplicateResourceException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public DuplicateResourceException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public DuplicateResourceException(String field, Object value) {
        super(String.format("%s '%s' já está em uso", field, value));
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
}