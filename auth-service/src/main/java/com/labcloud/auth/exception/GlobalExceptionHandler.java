package com.labcloud.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.labcloud.auth.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // ====== 404 NOT FOUND ======
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                        HttpServletRequest request) {

                log.warn("Recurso não encontrado: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value()).error("Resource Not Found")
                                .message(ex.getMessage()).path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // ====== 409 CONFLICT ======
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex,
                        HttpServletRequest request) {

                log.warn("Recurso duplicado: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value()).error("Resource Already Exists")
                                .message(ex.getMessage()).path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // ====== 400 BAD REQUEST (validação de campos) ======
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                log.warn("Erro de validação: {}", errors);

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value()).error("Validation Failed")
                                .message("Um ou mais campos estão inválidos").path(request.getRequestURI())
                                .validationErrors(errors).build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // ====== 400 BAD REQUEST (negócio) ======
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {

                log.warn("Erro de negócio: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value()).error("Business Error").message(ex.getMessage())
                                .path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // ====== 400 BAD REQUEST (validação customizada) ======
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleCustomValidation(ValidationException ex,
                        HttpServletRequest request) {

                log.warn("Erro de validação customizada: {}", ex.getErrors());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value()).error("Validation Failed")
                                .message(ex.getMessage()).path(request.getRequestURI()).validationErrors(ex.getErrors())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // ====== 401 UNAUTHORIZED ======
        @ExceptionHandler({ UnauthorizedException.class, BadCredentialsException.class })
        public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException ex, HttpServletRequest request) {

                log.warn("Não autorizado: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value()).error("Unauthorized")
                                .message("Credenciais inválidas ou token expirado").path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // ====== 401 AUTHENTICATION ======
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {

                log.warn("Erro de autenticação: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value()).error("Authentication Failed")
                                .message(ex.getMessage()).path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // ====== 403 FORBIDDEN ======
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {

                log.warn("Acesso negado: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value()).error("Access Denied")
                                .message("Você não tem permissão para acessar este recurso")
                                .path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // ====== 500 INTERNAL SERVER ERROR ======
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

                log.error("Erro interno do servidor: ", ex);

                ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).error("Internal Server Error")
                                .message("Ocorreu um erro inesperado. Tente novamente mais tarde.")
                                .path(request.getRequestURI()).build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}