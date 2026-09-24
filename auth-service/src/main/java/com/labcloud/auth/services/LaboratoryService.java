package com.labcloud.auth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labcloud.auth.dto.request.LaboratoryRequest;
import com.labcloud.auth.dto.response.LaboratoryResponse;
import com.labcloud.auth.exception.DuplicateResourceException;
import com.labcloud.auth.exception.ResourceNotFoundException;
import com.labcloud.auth.mappers.LaboratoryMapper;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.repositories.LaboratoryRepository;
import com.labcloud.auth.util.SlugUtils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor 
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;

    @Transactional 
    public LaboratoryResponse create (LaboratoryRequest request){
        log.info("Criando novo laboratório {}", request.getName());

        String tenantId = generateTenantId(request.getName());

        if (laboratoryRepository.existsByTenantId(tenantId)) {
            throw new DuplicateResourceException("Laboratório","tenantId",tenantId);
        }

        Laboratory laboratory = laboratoryMapper.toEntity(request);
        laboratory.updateTenantId(tenantId);
        laboratory.setActive(true);

        Laboratory saved = laboratoryRepository.save(laboratory);
        log.info("Laboratório criado com sucesso {} - {}",saved.getId(), saved.getName());

        return laboratoryMapper.toResponse(saved);
       
        
    }

    @Transactional(readOnly = true)
    public LaboratoryResponse findById(String id){
        log.info("Procurando Laboratório pelo Id: {}",id);

       Laboratory Laboratory = laboratoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Laboratório","Id",id));

        return laboratoryMapper.toResponse(Laboratory);
    }


    @Transactional(readOnly = true)
    public LaboratoryResponse findByTenantId(String tenantId) {
        log.info("Procurando o laboratório pelo Tenant ID: {}",tenantId);

        Laboratory laboratory = laboratoryRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratório","TenantId",tenantId));
        
        return laboratoryMapper.toResponse(laboratory);
    }

    @Transactional(readOnly = true)
    public List<LaboratoryResponse> findAll() {
        log.info("Buscando todos os laboratórios");

        return laboratoryRepository.findAll().stream()
                .map(laboratoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LaboratoryResponse> findActive() {
        log.info("Buscando todos os laboratórios ativos");

        return laboratoryRepository.findByActiveTrue().stream()
                .map(laboratoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional 
    public LaboratoryResponse update(String id, LaboratoryRequest request){
        log.info("Atualizando laboratório: {}", id);


        Laboratory laboratory = laboratoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Laboratório","Id",id));
        
        laboratoryMapper.updateEntity(request, laboratory);

        Laboratory updated = laboratoryRepository.save(laboratory);
            log.info("Laboratório atualizado com sucesso: {}",id);
        
        return laboratoryMapper.toResponse(updated);

    }

    @Transactional 
    public void delete (String id) {
        log.info("Desativando o laboratório : {}",id);

    
        Laboratory laboratory = laboratoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Laboratório","Id",id));

        laboratory.setActive(false);
        laboratoryRepository.save(laboratory);
        
        log.info("Laboratório desativado: {}",id);
    
    }

    @Transactional
    public void hardDelete (String id){
        log.info("Deletando o Laboratório permanentemente: {}");

        if (!laboratoryRepository.existsById(id)){
            throw new ResourceNotFoundException("Laboratório", "ID", id);
        }
        
        laboratoryRepository.deleteById(id);
        log.info("Laboratório removido permanentemente: {}",id);

    }
    


    //Método auxiliar

    private String generateTenantId(String name) {


        String base = SlugUtils.generateSlug(name);
        String tenantId = base;
        int counter = 1;

        while (laboratoryRepository.existsByTenantId(tenantId)) {
            tenantId = base + "-" + counter;
            counter++;
        }

        return tenantId;
    }
}
