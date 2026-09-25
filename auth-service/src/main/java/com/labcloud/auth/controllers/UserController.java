package com.labcloud.auth.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.labcloud.auth.dto.request.UserRequest;
import com.labcloud.auth.dto.response.UserResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("/api/users")
@Slf4j 
@RequiredArgsConstructor 
public class UserController {
    private final UserService userService;

    @PostMapping 
    public ResponseEntity<UserResponse> create (@Valid @RequestBody UserRequest request){
         log.info("POST /api/users - Criando usuário: {}", request.getEmail());

         UserResponse response = userService.create(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable String id) {
        log.info("GET /api/users/{} - Buscando por ID", id);
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{} - Buscando por email", email);
        UserResponse response = userService.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        log.info("GET /api/users - Buscando todos");
        List<UserResponse> responses = userService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<UserResponse>> findByLaboratory(@PathVariable String laboratoryId) {
        log.info("GET /api/users/laboratory/{} - Buscando por laboratório", laboratoryId);
        List<UserResponse> responses = userService.findByLaboratory(laboratoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<UserResponse>> findByTenantId(@PathVariable String tenantId) {
        log.info("GET /api/users/tenant/{} - Buscando por tenant", tenantId);
        List<UserResponse> responses = userService.findByTenantId(tenantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/laboratory/{laboratoryId}/role/{role}")
    public ResponseEntity<List<UserResponse>> findByLaboratoryAndRole(
            @PathVariable String laboratoryId,
            @PathVariable UserRole role) {
        log.info("GET /api/users/laboratory/{}/role/{} - Buscando por role", laboratoryId, role);
        List<UserResponse> responses = userService.findByLaboratoryAndRole(laboratoryId, role);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UserRequest request) {
        log.info("PUT /api/users/{} - Atualizando", id);
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("DELETE /api/users/{} - Deletando", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
