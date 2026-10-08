package com.labcloud.sample.services;

import com.labcloud.sample.clients.AuthClient;
import com.labcloud.sample.dto.external.UserClientResponse;
import com.labcloud.sample.dto.request.SampleRequest;
import com.labcloud.sample.dto.response.SampleResponse;
import com.labcloud.sample.exception.ResourceNotFoundException;
import com.labcloud.sample.mappers.SampleMapper;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.models.Sample;
import com.labcloud.sample.repositories.ExperimentRepository;
import com.labcloud.sample.repositories.SampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

    private final SampleRepository sampleRepository;
    private final ExperimentRepository experimentRepository;
    private final SampleMapper sampleMapper;
    private final AuthClient authClient;

    // Create
    @Transactional
    public SampleResponse create(SampleRequest request, String userId) {
        log.info("Criando nova amostra: {}", request.getName());

        // 1. Buscar experimento
        Experiment experiment = experimentRepository.findById(request.getExperimentId())
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", request.getExperimentId()));

        // 2. Buscar usuário via Feign
        UserClientResponse user = fetchUser(userId);

        // 3. Criar entidade
        Sample sample = sampleMapper.toEntity(request);
        sample.updateTenantId(experiment.getTenantId());
        sample.setExperiment(experiment);
        sample.setCreatedBy(userId);
        sample.setCreatedByName(user.getName());

        // 4. Salvar
        Sample saved = sampleRepository.save(sample);
        log.info("Amostra criada com sucesso: {} - {}", saved.getId(), saved.getName());

        return sampleMapper.toResponse(saved);
    }

    // Find
    @Transactional(readOnly = true)
    public SampleResponse findById(String id) {
        log.info("Buscando amostra por ID: {}", id);

        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amostra", "ID", id));

        return sampleMapper.toResponse(sample);
    }

    @Transactional(readOnly = true)
    public List<SampleResponse> findAll() {
        log.info("Buscando todas as amostras");
        return sampleRepository.findAll().stream().map(sampleMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SampleResponse findByIdWithExperiment(String id) {
        log.info("Buscando amostra com experimento: {}", id);

        Sample sample = sampleRepository.findByIdWithExperiment(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amostra", "ID", id));

        return sampleMapper.toResponse(sample);
    }

    @Transactional(readOnly = true)
    public List<SampleResponse> findByTenantId(String tenantId) {
        log.info("Buscando amostras do tenant: {}", tenantId);

        return sampleRepository.findByTenantId(tenantId).stream().map(sampleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SampleResponse> findByTenantIdPaged(String tenantId, Pageable pageable) {
        log.info("Buscando amostras do tenant com paginação: {}", tenantId);

        return sampleRepository.findByTenantId(tenantId, pageable).map(sampleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SampleResponse> findByExperiment(String experimentId) {
        log.info("Buscando amostras do experimento: {}", experimentId);

        return sampleRepository.findByExperimentId(experimentId).stream().map(sampleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SampleResponse> findByExperimentPaged(String experimentId, Pageable pageable) {
        log.info("Buscando amostras do experimento com paginação: {}", experimentId);

        return sampleRepository.findByExperimentId(experimentId, pageable).map(sampleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SampleResponse> findByType(String tenantId, String type) {
        log.info("Buscando amostras por tipo: {} no tenant: {}", type, tenantId);

        return sampleRepository.findByTenantIdAndType(tenantId, type).stream().map(sampleMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update
    @Transactional
    public SampleResponse update(String id, SampleRequest request) {
        log.info("Atualizando amostra: {}", id);

        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amostra", "ID", id));

        // Se experimentId mudou, atualizar
        if (request.getExperimentId() != null && !sample.getExperiment().getId().equals(request.getExperimentId())) {

            Experiment experiment = experimentRepository.findById(request.getExperimentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", request.getExperimentId()));
            sample.setExperiment(experiment);
            sample.updateTenantId(experiment.getTenantId());
        }

        sampleMapper.updateEntity(request, sample);

        Sample updated = sampleRepository.save(sample);
        log.info("Amostra atualizada: {}", updated.getId());

        return sampleMapper.toResponse(updated);
    }

    // Delete
    @Transactional
    public void delete(String id) {
        log.info("Deletando amostra: {}", id);

        if (!sampleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Amostra", "ID", id);
        }

        sampleRepository.deleteById(id);
        log.info("Amostra deletada: {}", id);
    }

    // Metodo auxiliar (Feign)
    private UserClientResponse fetchUser(String userId) {
        try {
            log.debug("Buscando usuário via Feign: {}", userId);
            return authClient.getUser(userId);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário: {}", e.getMessage());
            throw new ResourceNotFoundException("Usuário", "ID", userId);
        }
    }
}
