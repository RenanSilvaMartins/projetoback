package com.itb.inf2fm.projetoback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Async Service - Operações assíncronas para melhor performance
 * 
 * Otimizações:
 * - Logs não bloqueantes
 * - Operações de limpeza em background
 * - Processamento paralelo
 */
@Service
public class AsyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);
    
    @Async
    public void logAsync(String level, String message, Object... args) {
        String sanitizedMessage = sanitizeLogMessage(message);
        Object[] sanitizedArgs = sanitizeLogArgs(args);
        
        switch (level.toLowerCase()) {
            case "info":
                logger.info(sanitizedMessage, sanitizedArgs);
                break;
            case "warn":
                logger.warn(sanitizedMessage, sanitizedArgs);
                break;
            case "error":
                logger.error(sanitizedMessage, sanitizedArgs);
                break;
            default:
                logger.debug(sanitizedMessage, sanitizedArgs);
                break;
        }
    }
    
    private String sanitizeLogMessage(String message) {
        if (message == null) return "";
        return message.replaceAll("[\r\n]", "_");
    }
    
    private Object[] sanitizeLogArgs(Object... args) {
        if (args == null) return new Object[0];
        Object[] sanitized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                sanitized[i] = ((String) args[i]).replaceAll("[\r\n]", "_");
            } else {
                sanitized[i] = args[i];
            }
        }
        return sanitized;
    }
    
    @Async
    public void processInBackground(Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            logger.error("Erro no processamento assíncrono: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado no processamento assíncrono: {}", e.getMessage());
        }
    }
}