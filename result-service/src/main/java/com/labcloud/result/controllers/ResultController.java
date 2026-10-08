package com.labcloud.result.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.labcloud.result.services.ResultService;
import jakarta.validation.Valid;

import com.labcloud.common.security.SecurityUtils;
import com.labcloud.result.dto.request.ResultRequest;
import com.labcloud.result.dto.response.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("/api/results")
@Slf4j 
@RequiredArgsConstructor 
public class ResultController {

    private final ResultService resultService;

    //Create
    @PostMapping 
     public ResponseEntity<ResultResponse> create(@Valid @RequestBody ResultRequest request) {
        log.info("POST /api/results - Criando resultado: {}", request.getParameter());

        //Pega o userId do JWT
        String userId = SecurityUtils.getCurrentUserId();

        ResultResponse response = resultService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ====== FIND ======
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> findById(@PathVariable String id) {
        log.info("GET /api/results/{} - Buscando por ID", id);
        ResultResponse response = resultService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ResultResponse>> findByTenantId(@PathVariable String tenantId) {
        log.info("GET /api/results/tenant/{} - Buscando por tenant", tenantId);
        List<ResultResponse> responses = resultService.findByTenantId(tenantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/paged")
    public ResponseEntity<Page<ResultResponse>> findByTenantIdPaged(
            @PathVariable String tenantId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/results/tenant/{}/paged - Buscando com paginação", tenantId);
        Page<ResultResponse> responses = resultService.findByTenantIdPaged(tenantId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sample/{sampleId}")
    public ResponseEntity<List<ResultResponse>> findBySample(@PathVariable String sampleId) {
        log.info("GET /api/results/sample/{} - Buscando por amostra", sampleId);
        List<ResultResponse> responses = resultService.findBySample(sampleId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sample/{sampleId}/paged")
    public ResponseEntity<Page<ResultResponse>> findBySamplePaged(
            @PathVariable String sampleId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/results/sample/{}/paged - Buscando com paginação", sampleId);
        Page<ResultResponse> responses = resultService.findBySamplePaged(sampleId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/experiment/{experimentId}")
    public ResponseEntity<List<ResultResponse>> findByExperiment(@PathVariable String experimentId) {
        log.info("GET /api/results/experiment/{} - Buscando por experimento", experimentId);
        List<ResultResponse> responses = resultService.findByExperiment(experimentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/experiment/{experimentId}/paged")
    public ResponseEntity<Page<ResultResponse>> findByExperimentPaged(
            @PathVariable String experimentId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/results/experiment/{}/paged - Buscando com paginação", experimentId);
        Page<ResultResponse> responses = resultService.findByExperimentPaged(experimentId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/parameter/{parameter}")
    public ResponseEntity<List<ResultResponse>> findByParameter(
            @PathVariable String tenantId,
            @PathVariable String parameter) {
        log.info("GET /api/results/tenant/{}/parameter/{} - Buscando por parâmetro", tenantId, parameter);
        List<ResultResponse> responses = resultService.findByParameter(tenantId, parameter);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/valid")
    public ResponseEntity<List<ResultResponse>> findValidResults(@PathVariable String tenantId) {
        log.info("GET /api/results/tenant/{}/valid - Buscando válidos", tenantId);
        List<ResultResponse> responses = resultService.findValidResults(tenantId);
        return ResponseEntity.ok(responses);
    }

    // ====== UPDATE ======
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ResultRequest request) {
        log.info("PUT /api/results/{} - Atualizando", id);
        ResultResponse response = resultService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ====== DELETE ======
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("DELETE /api/results/{} - Deletando", id);
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    } 

}
