package com.itb.inf2fm.projetoback.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
