package com.itb.inf2fm.projetoback.util;

import java.util.regex.Pattern;

/**
 * Utilitários para validação de dados
 * Centraliza validações comuns utilizadas em toda a aplicação
 */
public class ValidationUtils {

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern CPF_PATTERN = Pattern.compile(
        "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$"
    );

    private static final Pattern CNPJ_PATTERN = Pattern.compile(
        "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$|^\\d{14}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$"
    );

    private static final Pattern CEP_PATTERN = Pattern.compile(
        "^\\d{5}-?\\d{3}$"
    );

    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida CPF com verificação de dígitos
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;
        
        // Remove formatação
        cpf = cpf.replaceAll("[^\\d]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        // Validação dos dígitos verificadores
        try {
            int[] digits = cpf.chars().map(c -> c - '0').toArray();
            
            // Primeiro dígito
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += digits[i] * (10 - i);
            }
            int digit1 = 11 - (sum1 % 11);
            if (digit1 >= 10) digit1 = 0;
            
            // Segundo dígito
            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += digits[i] * (11 - i);
            }
            int digit2 = 11 - (sum2 % 11);
            if (digit2 >= 10) digit2 = 0;
            
            return digits[9] == digit1 && digits[10] == digit2;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida CNPJ com verificação de dígitos
     */
    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) return false;
        
        // Remove formatação
        cnpj = cnpj.replaceAll("[^\\d]", "");
        
        // Verifica se tem 14 dígitos
        if (cnpj.length() != 14) return false;
        
        // Verifica se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) return false;
        
        try {
            int[] digits = cnpj.chars().map(c -> c - '0').toArray();
            
            // Primeiro dígito
            int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int sum1 = 0;
            for (int i = 0; i < 12; i++) {
                sum1 += digits[i] * weight1[i];
            }
            int digit1 = 11 - (sum1 % 11);
            if (digit1 >= 10) digit1 = 0;
            
            // Segundo dígito
            int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int sum2 = 0;
            for (int i = 0; i < 13; i++) {
                sum2 += digits[i] * weight2[i];
            }
            int digit2 = 11 - (sum2 % 11);
            if (digit2 >= 10) digit2 = 0;
            
            return digits[12] == digit1 && digits[13] == digit2;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida formato de telefone brasileiro
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Valida formato de CEP
     */
    public static boolean isValidCEP(String cep) {
        return cep != null && CEP_PATTERN.matcher(cep.trim()).matches();
    }

    /**
     * Valida força da senha
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) >= 0);
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Valida se string não é nula nem vazia
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valida se string tem tamanho dentro do intervalo
     */
    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= min && length <= max;
    }

    /**
     * Remove formatação de CPF/CNPJ
     */
    public static String removeFormatting(String document) {
        return document != null ? document.replaceAll("[^\\d]", "") : null;
    }

    /**
     * Formata CPF
     */
    public static String formatCPF(String cpf) {
        if (cpf == null) return null;
        cpf = removeFormatting(cpf);
        if (cpf.length() != 11) return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Formata CNPJ
     */
    public static String formatCNPJ(String cnpj) {
        if (cnpj == null) return null;
        cnpj = removeFormatting(cnpj);
        if (cnpj.length() != 14) return cnpj;
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    /**
     * Formata telefone
     */
    public static String formatPhone(String phone) {
        if (phone == null) return null;
        phone = removeFormatting(phone);
        
        if (phone.length() == 10) {
            return phone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        } else if (phone.length() == 11) {
            return phone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        
        return phone;
    }

    /**
     * Formata CEP
     */
    public static String formatCEP(String cep) {
        if (cep == null) return null;
        cep = removeFormatting(cep);
        if (cep.length() != 8) return cep;
        return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    /**
     * Gera mensagem de erro padronizada para validação
     */
    public static String getValidationMessage(String field, String error) {
        return String.format("Campo '%s': %s", field, error);
    }
}
