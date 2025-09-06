/**
 * Exceção Base do Sistema
 * 
 * Classe base para todas as exceções customizadas da aplicação
 * 
 * @author Equipe de Desenvolvimento
 * @version 2.0.0
 */
package com.itb.inf2fm.projetoback.exceptions;

/**
 * Exceção base para todas as exceções da aplicação
 */
public abstract class BaseException extends RuntimeException {
    
    private final String userMessage;
    private final String errorCode;
    
    protected BaseException(String message, String userMessage, String errorCode) {
        super(message);
        this.userMessage = userMessage;
        this.errorCode = errorCode;
    }
    
    protected BaseException(String message, String userMessage, String errorCode, Throwable cause) {
        super(message, cause);
        this.userMessage = userMessage;
        this.errorCode = errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
