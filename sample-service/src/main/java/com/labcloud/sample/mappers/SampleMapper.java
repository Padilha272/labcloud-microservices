package com.labcloud.sample.mappers;

import org.springframework.stereotype.Component;
import com.labcloud.sample.models.Sample;
import com.labcloud.sample.dto.request.SampleRequest;
import com.labcloud.sample.dto.response.SampleResponse;

@Component 
public class SampleMapper {

    //Converter de requisição para entidade
    public Sample toEntity(SampleRequest request){
        if(request ==null ) return null;

        return Sample.builder()
                .name(request.getName())
                .type(request.getType())
                .collectionDate(request.getCollectionDate())
                .collectionMethod(request.getCollectionMethod())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .storageConditions(request.getStorageConditions())
                .location(request.getLocation())
                .notes(request.getNotes())
                .build();
    }

    //Converter de entidade para resposta
    public SampleResponse toResponse(Sample sample) {
        if (sample == null) return null;

        return SampleResponse.builder()
                .id(sample.getId())
                .name(sample.getName())
                .type(sample.getType())
                .collectionDate(sample.getCollectionDate())
                .collectionMethod(sample.getCollectionMethod())
                .quantity(sample.getQuantity())
                .unit(sample.getUnit())
                .storageConditions(sample.getStorageConditions())
                .location(sample.getLocation())
                .notes(sample.getNotes())
                .tenantId(sample.getTenantId())
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .experimentId(sample.getExperiment() != null ? sample.getExperiment().getId() : null)
                .experimentName(sample.getExperiment() != null ? sample.getExperiment().getName() : null)
                .createdBy(sample.getCreatedBy())
                .createdByName(sample.getCreatedByName())
                .build();
    }

    //Update
    public void updateEntity(SampleRequest request, Sample sample) {
        if (request == null || sample == null) return;

        if (request.getName() != null) {
            sample.setName(request.getName());
        }
        if (request.getType() != null) {
            sample.setType(request.getType());
        }
        if (request.getCollectionDate() != null) {
            sample.setCollectionDate(request.getCollectionDate());
        }
        if (request.getCollectionMethod() != null) {
            sample.setCollectionMethod(request.getCollectionMethod());
        }
        if (request.getQuantity() != null) {
            sample.setQuantity(request.getQuantity());
        }
        if (request.getUnit() != null) {
            sample.setUnit(request.getUnit());
        }
        if (request.getStorageConditions() != null) {
            sample.setStorageConditions(request.getStorageConditions());
        }
        if (request.getLocation() != null) {
            sample.setLocation(request.getLocation());
        }
        if (request.getNotes() != null) {
            sample.setNotes(request.getNotes());
        }
        
    }
}
