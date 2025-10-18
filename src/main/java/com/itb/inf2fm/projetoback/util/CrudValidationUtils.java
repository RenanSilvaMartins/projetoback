package com.itb.inf2fm.projetoback.util;

import com.itb.inf2fm.projetoback.exception.ValidationException;
import com.itb.inf2fm.projetoback.exception.DuplicateResourceException;
import com.itb.inf2fm.projetoback.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utilitários para validação em operações CRUD
 */
public class CrudValidationUtils {
    
    /**
     * Valida se um ID é válido
     */
    public static void validateId(Long id, String resourceName) {
        if (id == null) {
            throw new ValidationException("ID não pode ser nulo");
        }
        if (id <= 0) {
            throw new ValidationException("ID deve ser maior que zero");
        }
    }
    
    /**
     * Valida se um recurso existe, lança exceção se não existir
     */
    public static <T> T validateResourceExists(Supplier<T> finder, String resourceName, Object id) {
        T resource = finder.get();
        if (resource == null) {
            throw new ResourceNotFoundException(resourceName, "id", id);
        }
        return resource;
    }
    
    /**
     * Valida se um recurso não existe (para evitar duplicatas)
     */
    public static void validateResourceNotExists(Supplier<Boolean> checker, String field, Object value) {
        if (checker.get()) {
            throw new DuplicateResourceException(field, value);
        }
    }
    
    /**
     * Valida campos obrigatórios
     */
    public static void validateRequiredFields(Map<String, Object> fields) {
        Map<String, String> errors = new HashMap<>();
        
        fields.forEach((fieldName, value) -> {
            if (value == null) {
                errors.put(fieldName, "Campo obrigatório");
            } else if (value instanceof String && ((String) value).trim().isEmpty()) {
                errors.put(fieldName, "Campo não pode estar vazio");
            }
        });
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Campos obrigatórios não preenchidos", errors);
        }
    }
    
    /**
     * Valida formato de email
     */
    public static void validateEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Formato de email inválido");
        }
    }
    
    /**
     * Valida formato de CPF
     */
    public static void validateCpf(String cpf) {
        if (cpf == null) return;
        
        // Remove caracteres não numéricos
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCpf.length() != 11) {
            throw new ValidationException("CPF deve conter 11 dígitos");
        }
        
        // Verifica se todos os dígitos são iguais
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            throw new ValidationException("CPF inválido");
        }
        
        // Validação dos dígitos verificadores
        if (!isValidCpf(cleanCpf)) {
            throw new ValidationException("CPF inválido");
        }
    }
    
    private static boolean isValidCpf(String cpf) {
        // Cálculo do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        
        // Cálculo do segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        
        return Character.getNumericValue(cpf.charAt(9)) == firstDigit &&
               Character.getNumericValue(cpf.charAt(10)) == secondDigit;
    }
}