package com.itb.inf2fm.projetoback.exceptions;

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

    ZoneId zoneBrasil = ZoneId.of("America/Sao_Paulo");


    String [] arrayMessage;

    // Código Erro : 500

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> globalException(Exception ex, WebRequest request) {

        LocalDateTime localDateTimeBrasil = LocalDateTime.now(zoneBrasil);
        String errorMessageDescription = ex.getLocalizedMessage(); // Mensagem detalhada do erro
        System.out.println(errorMessageDescription);               // Mostrando a mensagem detalhada do erro
        errorMessageDescription = "Ocorreu um erro interno no servidor"; // Substituindo a mensagem original por uma genérica
        arrayMessage = errorMessageDescription.split(":");
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Código Erro : 400

    @ExceptionHandler(value = {BadRequest.class})
    public ResponseEntity<Object> badRequestException(BadRequest ex, WebRequest request) {

        LocalDateTime localDateTimeBrasil = LocalDateTime.now(zoneBrasil);
        String errorMessageDescription = ex.getLocalizedMessage(); // Mensagem detalhada do erro
        System.out.println(errorMessageDescription);               // Mostrando a mensagem detalhada do erro
        if(errorMessageDescription == null) errorMessageDescription = ex.toString();
        arrayMessage = errorMessageDescription.split(":");
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // Código Erro : 404

    @ExceptionHandler(value = {NotFound.class})
    public ResponseEntity<Object> notFoundException(NotFound ex, WebRequest request) {

        LocalDateTime localDateTimeBrasil = LocalDateTime.now(zoneBrasil);
        String errorMessageDescription = ex.getLocalizedMessage(); // Mensagem detalhada do erro
        System.out.println(errorMessageDescription);               // Mostrando a mensagem detalhada do erro
        if(errorMessageDescription == null) errorMessageDescription = ex.toString();
        arrayMessage = errorMessageDescription.split(":");
        ErrorMessage errorMessage = new ErrorMessage(localDateTimeBrasil, arrayMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
