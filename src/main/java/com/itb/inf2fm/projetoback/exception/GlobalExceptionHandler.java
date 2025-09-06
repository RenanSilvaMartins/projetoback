package com.itb.inf2fm.projetoback.exception;

import com.itb.inf2fm.projetoback.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para toda a aplicação
 * Centraliza o tratamento de erros e padroniza as respostas
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private String sanitizeForLog(String input) {
        if (input == null) return "";
        return input.replaceAll("[\r\n]", "_");
    }

    /**
     * Trata exceções de validação de campos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.error(
            "Dados inválidos fornecidos", 
            "Verifique os campos e tente novamente"
        );
        response.setData(errors);
        response.setPath(request.getRequestURI());

        logger.warn("Erro de validação na rota {}: {}", request.getRequestURI(), errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata exceções de violação de constraints do banco de dados
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Violação de regra de negócio",
            ex.getMessage()
        );
        response.setPath(request.getRequestURI());

        logger.warn("Violação de constraint na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata exceções de integridade de dados
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        String message = "Erro de integridade dos dados";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Dados duplicados: registro já existe";
        } else if (ex.getMessage().contains("foreign key")) {
            message = "Violação de chave estrangeira: registro referenciado";
        }

        ApiResponse<Object> response = ApiResponse.error(message, "Verifique os dados e tente novamente");
        response.setPath(request.getRequestURI());

        logger.warn("Erro de integridade na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata exceções de autenticação
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Falha na autenticação",
            "Credenciais inválidas"
        );
        response.setPath(request.getRequestURI());

        logger.warn("Tentativa de autenticação inválida na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Trata exceções de acesso negado
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Acesso negado",
            "Você não tem permissão para acessar este recurso"
        );
        response.setPath(request.getRequestURI());

        logger.warn("Acesso negado na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Trata exceções de recurso não encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Recurso não encontrado",
            ex.getMessage()
        );
        response.setPath(request.getRequestURI());

        logger.info("Recurso não encontrado na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Trata exceções de regra de negócio
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Erro de regra de negócio",
            ex.getMessage()
        );
        response.setPath(request.getRequestURI());

        logger.warn("Erro de regra de negócio na rota {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata todas as outras exceções não previstas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        ApiResponse<Object> response = ApiResponse.error(
            "Erro interno do servidor",
            "Ocorreu um erro inesperado. Tente novamente mais tarde."
        );
        response.setPath(request.getRequestURI());

        String sanitizedUri = sanitizeForLog(request.getRequestURI());
        String sanitizedMessage = sanitizeForLog(ex.getMessage());
        logger.error("Erro interno na rota {}: {}", sanitizedUri, sanitizedMessage);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
