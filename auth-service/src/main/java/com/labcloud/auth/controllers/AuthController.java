package com.labcloud.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.labcloud.auth.dto.request.AuthRequest;
import com.labcloud.auth.dto.request.RegisterRequest;
import com.labcloud.auth.dto.response.AuthResponse;
import com.labcloud.auth.services.AuthService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("api/auth")
@Slf4j 
@RequiredArgsConstructor 
public class AuthController {

    
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody AuthRequest request) {
        log.info("POST /api/auth/login - Login: {}", request.getEmail());


        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Post /api/auth/register - Registrando: {}",request.getEmail());

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
