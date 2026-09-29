package com.labcloud.sample.mappers;

import org.springframework.stereotype.Component;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.dto.request.*;
import com.labcloud.sample.dto.response.ExperimentResponse;
import com.labcloud.sample.enums.ExperimentStatus;

@Component 
public class ExperimentMapper {

    //Converter de requisição para entidade
    public Experiment toEntity(ExperimentRequest request){

        if (request == null) return null;

        return Experiment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : ExperimentStatus.PLANNED)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .objective(request.getObjective())
                .methodology(request.getMethodology())
                .laboratoryId(request.getLaboratoryId())
                .build();

    }

    //Converter de entidade para resposta
    public ExperimentResponse toResponse(Experiment experiment){

        if (experiment == null) return null;

        return ExperimentResponse.builder()
                        .id(experiment.getId())
                        .tenantId(experiment.getTenantId())
                        .name(experiment.getName())
                        .description(experiment.getDescription())
                        .status(experiment.getStatus())
                        .startDate(experiment.getStartDate())
                        .endDate(experiment.getEndDate())
                        .objective(experiment.getObjective())
                        .methodology(experiment.getMethodology())
                        .createdAt(experiment.getCreatedAt())
                        .updatedAt(experiment.getUpdatedAt())
                        .laboratoryId(experiment.getLaboratoryId())
                        .laboratoryName(experiment.getLaboratoryName())
                        .createdBy(experiment.getCreatedBy())
                        .createdByName(experiment.getCreatedByName())
                        .totalSamples(experiment.getSamples() != null ? experiment.getSamples().size() : 0)
                        .build();
    }


    //Update
    public void updateEntity(ExperimentRequest request, Experiment experiment){
        if (request == null | experiment ==null) return;

        if (request.getName() !=null) {
                experiment.setName(request.getName());
            }
        if (request.getDescription() != null) {
            experiment.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            experiment.setStatus(request.getStatus());
        }
        if (request.getStartDate() != null) {
            experiment.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            experiment.setEndDate(request.getEndDate());
        }
        if (request.getObjective() != null) {
            experiment.setObjective(request.getObjective());
        }
        if (request.getMethodology() != null) {
            experiment.setMethodology(request.getMethodology());
        }
        
    }


}