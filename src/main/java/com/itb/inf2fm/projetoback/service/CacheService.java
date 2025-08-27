package com.itb.inf2fm.projetoback.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cache Service - Cache em memória para otimização de performance
 * 
 * Otimizações:
 * - Cache de consultas frequentes
 * - Redução de queries ao banco
 * - Expiração automática
 */
@Service
public class CacheService {
    
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final long DEFAULT_TTL = 300_000; // 5 minutos
    
    public CacheService() {
        // Limpeza automática a cada minuto
        scheduler.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.MINUTES);
    }
    
    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }
    
    public void put(String key, Object value, long ttlMillis) {
        long expiry = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry(value, expiry));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.value;
        }
        cache.remove(key);
        return null;
    }
    
    public void remove(String key) {
        cache.remove(key);
    }
    
    public void clear() {
        cache.clear();
    }
    
    private void cleanup() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    @PreDestroy
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    private static class CacheEntry {
        final Object value;
        final long expiry;
        
        CacheEntry(Object value, long expiry) {
            this.value = value;
            this.expiry = expiry;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiry;
        }
    }
}