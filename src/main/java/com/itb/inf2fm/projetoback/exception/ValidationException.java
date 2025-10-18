package com.itb.inf2fm.projetoback.exception;

import java.util.Map;

/**
 * Exceção para erros de validação de dados
 */
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = null;
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}