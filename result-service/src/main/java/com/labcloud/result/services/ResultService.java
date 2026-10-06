package com.labcloud.result.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labcloud.result.repositories.ResultRepository;



import com.labcloud.result.mappers.ResultMapper;
import com.labcloud.result.models.Result;
import com.labcloud.result.clients.*;
import com.labcloud.result.dto.response.ResultResponse;
import com.labcloud.result.dto.external.SampleClientResponse;
import com.labcloud.result.exception.*;
import com.labcloud.result.dto.external.UserClientResponse;
import com.labcloud.result.dto.request.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor 
public class ResultService {

    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;
    private final AuthClient authClient;
    private final SampleClient sampleClient;

    //Criar
    @Transactional 
    public ResultResponse create(ResultRequest request,String userId) {
        log.info("Criando novo resultado para amostra: {}",request.getSampleId());

        //1.Buscar dados da amostra via feign
        SampleClientResponse sample = fetchSample(request.getSampleId());

        //2.Buscar dados do usuário via feign
        UserClientResponse user = fetchUser(userId);

        //3.Criar entidade
        Result result = resultMapper.toEntity(request);
        result.updateTenantId(sample.getTenantId());
        result.setSampleName(sample.getName());
        result.setExperimentId(sample.getExperimentId());
        result.setExperimentName(sample.getExperimentName());
        result.setCreatedBy(userId);
        result.setCreatedByName(user.getName());

        //4.Se measurementDate não veio, usar agora 
        if (result.getMeasurementDate()==null){
            result.setMeasurementDate(LocalDateTime.now());
        }

        //5.Salvar 
        Result saved = resultRepository.save(result);
        log.info("Resultado criado com sucesso: {} - {}", saved.getId(), saved.getParameter());

        return resultMapper.toResponse(saved);



    }

    //Buscar
    @Transactional(readOnly=true)
    private ResultResponse findById(String id){
        log.info("Buscando resultado pelo id: {}",id);
        Result result = resultRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException("Result","Id",id));
        return resultMapper.toResponse(result);
    }

    @Transactional(readOnly = true)
    private List<ResultResponse> findByTenantId(String tenantId){
        log.info("Buscando os resultados do tenant: {}", tenantId);
        return resultRepository.findByTenantId(tenantId).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private Page<ResultResponse> findByTenantIdPaged(String tenantId,Pageable pageable){
        log.info("Buscando resultados do tenant com paginação: {}", tenantId);
        return resultRepository.findByTenantId(tenantId, pageable)
                .map(resultMapper::toResponse);
        
    }

    @Transactional(readOnly = true)
    private List<ResultResponse> findBySample(String sampleId) {
        log.info("Buscando resultados da amostra: {}", sampleId);

        return resultRepository.findBySampleId(sampleId).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public Page<ResultResponse> findBySamplePaged(String sampleId, Pageable pageable) {
        log.info("Buscando resultados da amostra com paginação: {}", sampleId);

        return resultRepository.findBySampleId(sampleId, pageable)
                .map(resultMapper::toResponse);
    }

     @Transactional(readOnly = true)
    public List<ResultResponse> findByExperiment(String experimentId) {
        log.info("Buscando resultados do experimento: {}", experimentId);

        return resultRepository.findByExperimentId(experimentId).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ResultResponse> findByExperimentPaged(String experimentId, Pageable pageable) {
        log.info("Buscando resultados do experimento com paginação: {}", experimentId);

        return resultRepository.findByExperimentId(experimentId, pageable)
                .map(resultMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ResultResponse> findByParameter(String tenantId, String parameter) {
        log.info("Buscando resultados do parâmetro: {} no tenant: {}", parameter, tenantId);

        return resultRepository.findByTenantIdAndParameter(tenantId, parameter).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResultResponse> findValidResults(String tenantId) {
        log.info("Buscando resultados válidos no tenant: {}", tenantId);

        return resultRepository.findByTenantIdAndIsValidTrue(tenantId).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update
    @Transactional
    public ResultResponse update(String id, ResultRequest request) {
        log.info("Atualizando resultado: {}", id);

        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado", "ID", id));

        // Se sampleId mudou, atualizar dados desnormalizados
        if (request.getSampleId() != null &&
                !result.getSampleId().equals(request.getSampleId())) {

            SampleClientResponse sample = fetchSample(request.getSampleId());
            result.setSampleId(request.getSampleId());
            result.setSampleName(sample.getName());
            result.setExperimentId(sample.getExperimentId());
            result.setExperimentName(sample.getExperimentName());
            result.updateTenantId(sample.getTenantId());
        }

        resultMapper.updateEntity(request, result);

        Result updated = resultRepository.save(result);
        log.info("Resultado atualizado: {}", updated.getId());

        return resultMapper.toResponse(updated);
    }

    // Delete
    @Transactional
    public void delete(String id) {
        log.info("Deletando resultado: {}", id);

        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resultado", "ID", id);
        }

        resultRepository.deleteById(id);
        log.info("Resultado deletado: {}", id);
    }




    //Métodos auxiliares

    private SampleClientResponse fetchSample(String sampleId){
        try{
            log.debug("Buscando sample via feign: {}",sampleId);
            return sampleClient.getSample(sampleId);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário: {}",sampleId);
            throw new ResourceNotFoundException("Usuário","Id",sampleId);
        }

    }

    private UserClientResponse fetchUser(String userId){
        try{
            log.debug("Buscando usuário via feign: {}",userId);
            return authClient.getUser(userId);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário: {}",userId);
            throw new ResourceNotFoundException("Usuário","Id",userId);
        }
    }
}
