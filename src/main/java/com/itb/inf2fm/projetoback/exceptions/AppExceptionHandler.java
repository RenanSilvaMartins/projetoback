package com.itb.inf2fm.projetoback.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);
    private static final ZoneId ZONE_BRASIL = ZoneId.of("America/Sao_Paulo");
    
    private String sanitizeForLog(String input) {
        if (input == null) return "";
        return input.replaceAll("[\r\n]", "_");
    }
    
    // Código Erro : 500

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> globalException(Exception ex, WebRequest request) {
        LocalDateTime localDateTimeBrasil = LocalDateTime.now(ZONE_BRASIL);
        logger.error("Erro interno do servidor", ex);
        String errorMessageDescription = "Ocorreu um erro interno no servidor";
        String[] arrayMessage = {errorMessageDescription};
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Código Erro : 400

    @ExceptionHandler(value = {BadRequest.class})
    public ResponseEntity<Object> badRequestException(BadRequest ex, WebRequest request) {
        LocalDateTime localDateTimeBrasil = LocalDateTime.now(ZONE_BRASIL);
        logger.warn("Bad request: {}", sanitizeForLog(ex.getMessage()));
        String errorMessageDescription = ex.getLocalizedMessage();
        if(errorMessageDescription == null) errorMessageDescription = ex.toString();
        String[] arrayMessage = {errorMessageDescription};
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NotFound.class})
    public ResponseEntity<Object> notFoundException(NotFound ex, WebRequest request) {
        LocalDateTime localDateTimeBrasil = LocalDateTime.now(ZONE_BRASIL);
        logger.info("Resource not found: {}", sanitizeForLog(ex.getMessage()));
        String errorMessageDescription = ex.getLocalizedMessage();
        if(errorMessageDescription == null) errorMessageDescription = ex.toString();
        String[] arrayMessage = {errorMessageDescription};
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
