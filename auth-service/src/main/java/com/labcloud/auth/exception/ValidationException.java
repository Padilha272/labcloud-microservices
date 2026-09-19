package com.labcloud.auth.exception;

import java.util.Map;

import lombok.Getter;

@Getter 
public class ValidationException extends RuntimeException{

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("Erro de validação");
        this.errors = errors;
    }
}
