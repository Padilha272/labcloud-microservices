package com.labcloud.auth.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.labcloud.auth.dto.request.LaboratoryRequest;
import com.labcloud.auth.dto.response.LaboratoryResponse;
import com.labcloud.auth.services.LaboratoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/laboratories")
@Slf4j 
@RequiredArgsConstructor 
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @PostMapping
    public ResponseEntity<LaboratoryResponse> create (@Valid @RequestBody LaboratoryRequest request) {

        log.info("POST /api/laboratories - criando: {}",request.getName());
        LaboratoryResponse response = laboratoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> findById (@PathVariable String id){
        log.info("GET /api/laboratories/{} - Buscando por ID", id);

        LaboratoryResponse response = laboratoryService.findById(id);

        return ResponseEntity.ok(response);
       
    }

    @GetMapping("/tenantId/{tenantId}")
    public ResponseEntity<LaboratoryResponse> findByTenantId (@PathVariable String tenantId) {

        log.info("GET /api/laboratories/tenant/{} - Buscando por tenant", tenantId);

        LaboratoryResponse reponse = laboratoryService.findByTenantId(tenantId);

        return ResponseEntity.ok(reponse);

    }

    @GetMapping
    public ResponseEntity<List<LaboratoryResponse>> findAll() {
        log.info("GET /api/laboratories - Buscando todos");
        List<LaboratoryResponse> responses = laboratoryService.findAll();
        return ResponseEntity.ok(responses);

    }

    @GetMapping("/active")
    public ResponseEntity<List<LaboratoryResponse>> findActive () {
        log.info("GET /api/laboratories/active - Buscando ativos");

        List<LaboratoryResponse> responses = laboratoryService.findActive();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> update (
            @PathVariable String id,
            @Valid @RequestBody LaboratoryRequest request) {
        log.info("PUT /api/laboratories/{} - Atualizando", id);

        LaboratoryResponse response = laboratoryService.update(id, request);
        return ResponseEntity.ok(response);      
        }


    @PostMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> delete (@PathVariable String id) {
        log.info("PUT /api/laboratories/{} - Atualizando", id);

        laboratoryService.delete(id);
        return ResponseEntity.noContent().build();      
        }
}
