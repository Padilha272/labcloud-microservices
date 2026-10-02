package com.labcloud.sample.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.labcloud.sample.security.SecurityUtils;
import com.labcloud.sample.dto.request.ExperimentRequest;
import com.labcloud.sample.dto.response.ExperimentResponse;
import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.services.ExperimentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController
@RequestMapping("/api/experiments")
@RequiredArgsConstructor 
public class ExperimentController {
    private final ExperimentService experimentService;

    //Criar o experimento
    @PostMapping 
    public ResponseEntity<ExperimentResponse> create (@Valid @RequestBody ExperimentRequest request) {

        log.info("POST /api/experiments - Criando experimento: {}", request.getName());

        //Pega o userId do JWT (via SecurityUtils)
        String userId = SecurityUtils.getCurrentUserId();

        ExperimentResponse response = experimentService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Buscar
    @GetMapping("/{id}")
    public ResponseEntity<ExperimentResponse> findById (@PathVariable String id){
        log.info("Buscando experimento por id",id);

        ExperimentResponse response = experimentService.findById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/details")
    public ResponseEntity<ExperimentResponse> findByIdWithSamples(@PathVariable String id) {
        log.info("GET /api/experiments/{}/details - Buscando com amostras", id);
        ExperimentResponse response = experimentService.findByIdWithSamples(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ExperimentResponse>> findByTenantId(@PathVariable String tenantId) {
        log.info("GET /api/experiments/tenant/{} - Buscando por tenant", tenantId);
        List<ExperimentResponse> responses = experimentService.findByTenantId(tenantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/paged")
    public ResponseEntity<Page<ExperimentResponse>> findByTenantIdPaged(
            @PathVariable String tenantId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/experiments/tenant/{}/paged - Buscando com paginação", tenantId);
        Page<ExperimentResponse> responses = experimentService.findByTenantIdPaged(tenantId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<ExperimentResponse>> findByLaboratory(@PathVariable String laboratoryId) {
        log.info("GET /api/experiments/laboratory/{} - Buscando por laboratório", laboratoryId);
        List<ExperimentResponse> responses = experimentService.findByLaboratory(laboratoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/status/{status}")
    public ResponseEntity<List<ExperimentResponse>> findByStatus(
            @PathVariable String tenantId,
            @PathVariable ExperimentStatus status) {
        log.info("GET /api/experiments/tenant/{}/status/{} - Buscando por status", tenantId, status);
        List<ExperimentResponse> responses = experimentService.findByStatus(tenantId, status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<List<ExperimentResponse>> findActive(@PathVariable String tenantId) {
        log.info("GET /api/experiments/tenant/{}/active - Buscando ativos", tenantId);
        List<ExperimentResponse> responses = experimentService.findActive(tenantId);
        return ResponseEntity.ok(responses);
    }


     //Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<ExperimentResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ExperimentRequest request) {
        log.info("PUT /api/experiments/{} - Atualizando", id);
        ExperimentResponse response = experimentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    //Ações de negócio
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ExperimentResponse> activate(@PathVariable String id) {
        log.info("PATCH /api/experiments/{}/activate - Ativando", id);
        ExperimentResponse response = experimentService.activate(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ExperimentResponse> complete(@PathVariable String id) {
        log.info("PATCH /api/experiments/{}/complete - Completando", id);
        ExperimentResponse response = experimentService.complete(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ExperimentResponse> cancel(@PathVariable String id) {
        log.info("PATCH /api/experiments/{}/cancel - Cancelando", id);
        ExperimentResponse response = experimentService.cancel(id);
        return ResponseEntity.ok(response);
    }

    //Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("DELETE /api/experiments/{} - Deletando", id);
        experimentService.delete(id);
        return ResponseEntity.noContent().build();
    }






}
