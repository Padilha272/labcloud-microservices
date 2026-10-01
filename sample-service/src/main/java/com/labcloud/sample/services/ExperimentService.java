package com.labcloud.sample.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labcloud.sample.clients.AuthClient;
import com.labcloud.sample.dto.external.LaboratoryClientResponse;
import com.labcloud.sample.dto.external.UserClientResponse;
import com.labcloud.sample.dto.request.ExperimentRequest;
import com.labcloud.sample.dto.response.ExperimentResponse;
import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.exception.ResourceNotFoundException;
import com.labcloud.sample.mappers.ExperimentMapper;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.repositories.ExperimentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentService {

    private final ExperimentMapper experimentMapper;
    private final ExperimentRepository experimentRepository;
    private final AuthClient authClient;

    // Criação
    @Transactional
    public ExperimentResponse create(ExperimentRequest request, String userId) {
        log.info("Criando novo experimento: {}", userId);

        // I.Buscar dados do laboratório via Feign (Auth Service)
        LaboratoryClientResponse laboratory = fetchLaboratory(request.getLaboratoryId());

        // II.Buscar dados do usuário via Feign (Auth Service)
        UserClientResponse user = fetchUser(userId);

        // III.Criando entidade
        Experiment experiment = experimentMapper.toEntity(request);
        experiment.updateTenantId(laboratory.getTenantId());
        experiment.setLaboratoryName(laboratory.getName());
        experiment.setCreatedBy(userId);
        experiment.setCreatedByName(user.getName());

        // IV.Se o status for ACTIVE, ativar
        if (experiment.getStatus() == ExperimentStatus.ACTIVE) {
            experiment.activate();
        }

        // V.Salvar
        Experiment saved = experimentRepository.save(experiment);
        log.info("Experimento criado com sucesso: {} - {}", saved.getId(), saved.getName());

        return experimentMapper.toResponse(saved);
    }

    // Buscas
    @Transactional(readOnly = true)
    public ExperimentResponse findById(String id) {
        log.info("Buscando experimento pelo Id: {}", id);
        Experiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "Id", id));

        return experimentMapper.toResponse(experiment);


    }

    @Transactional(readOnly = true)
    public ExperimentResponse findByIdWithSamples(String id) {
        log.info("Buscando experimento com amostras: {}", id);

        Experiment experiment = experimentRepository.findByIdWithSamples(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", id));

        return experimentMapper.toResponse(experiment);
    }


    @Transactional(readOnly = true)
    public List<ExperimentResponse> findByTenantId(String tenantId) {
        log.info("Buscando experimentos do TenantId: {}", tenantId);
        
        return experimentRepository.findByTenantId(tenantId).stream()
                .map(experimentMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<ExperimentResponse> findByTenantIdPaged(String tenantId, Pageable pageable) {
        log.info("Buscando experimentos do tenant com paginação: {}", tenantId);

        return experimentRepository.findByTenantId(tenantId, pageable)
                .map(experimentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> findByLaboratory(String laboratoryId) {
        log.info("Buscando experimentos do laboratório: {}", laboratoryId);

        return experimentRepository.findByLaboratoryId(laboratoryId).stream()
                .map(experimentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> findByStatus(String tenantId, ExperimentStatus status) {
        log.info("Buscando experimentos por status: {} no tenant: {}", status, tenantId);

        return experimentRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(experimentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> findActive(String tenantId) {
        log.info("Buscando experimentos ativos no tenant: {}", tenantId);

        return experimentRepository.findActiveExperiments(tenantId).stream()
                .map(experimentMapper::toResponse)
                .collect(Collectors.toList());
    }

    //Update
    @Transactional
    public ExperimentResponse update(String id, ExperimentRequest request) {
        log.info("Atualizando experimento: {}", id);

        Experiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", id));

        // Se laboratoryId mudou, atualizar dados do lab
        if (request.getLaboratoryId() != null &&
                !experiment.getLaboratoryId().equals(request.getLaboratoryId())) {

            LaboratoryClientResponse laboratory = fetchLaboratory(request.getLaboratoryId());
            experiment.setLaboratoryId(request.getLaboratoryId());
            experiment.setLaboratoryName(laboratory.getName());
            experiment.updateTenantId(laboratory.getTenantId());
        }

        experimentMapper.updateEntity(request, experiment);

        Experiment updated = experimentRepository.save(experiment);
        log.info("Experimento atualizado: {}", updated.getId());

        return experimentMapper.toResponse(updated);
    }

    //Acões de negócio
    @Transactional
    public ExperimentResponse activate(String id) {
        log.info("Ativando experimento: {}", id);

        Experiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", id));

        experiment.activate();
        Experiment updated = experimentRepository.save(experiment);

        return experimentMapper.toResponse(updated);
    }

    @Transactional
    public ExperimentResponse complete(String id) {
        log.info("Completando experimento: {}", id);

        Experiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", id));

        experiment.complete();
        Experiment updated = experimentRepository.save(experiment);

        return experimentMapper.toResponse(updated);
    }

    @Transactional
    public ExperimentResponse cancel(String id) {
        log.info("Cancelando experimento: {}", id);

        Experiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experimento", "ID", id));

        experiment.cancel();
        Experiment updated = experimentRepository.save(experiment);

        return experimentMapper.toResponse(updated);
    }

    //Delete
    @Transactional
    public void delete(String id) {
        log.info("Deletando experimento: {}", id);

        if (!experimentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experimento", "ID", id);
        }

        experimentRepository.deleteById(id);
        log.info("Experimento deletado: {}", id);
    }



    // Métodos auxiliáres (Feign)
    private LaboratoryClientResponse fetchLaboratory(String laboratoryId) {
        try {
            log.debug("Buscando usuário via Feign: {}", laboratoryId);
            return authClient.getLaboratory(laboratoryId);
        } catch (Exception e) {
            log.error("Erro ao buscar laboratório: {}", e.getMessage());
            throw new ResourceNotFoundException("Laboratório", "Id", laboratoryId);
        }

    }

    private UserClientResponse fetchUser(String userId) {
        try {
            log.debug("Buscando usuário via Feign: {}", userId);
            return authClient.getUser(userId);
        } catch (Exception e) {
            log.error("Erro ao buscar o usuário: {}", userId);
            throw new ResourceNotFoundException("Usuário", "Id", userId);
        }
    }

}
