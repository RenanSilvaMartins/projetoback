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
        switch (level.toLowerCase()) {
            case "info":
                logger.info(message, args);
                break;
            case "warn":
                logger.warn(message, args);
                break;
            case "error":
                logger.error(message, args);
                break;
            default:
                logger.debug(message, args);
                break;
        }
    }
    
    @Async
    public void processInBackground(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.error("Erro no processamento assíncrono", e);
        }
    }
}