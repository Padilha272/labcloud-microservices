package com.labcloud.sample.dto.response;

import java.time.LocalDateTime;

import com.labcloud.sample.enums.ExperimentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class ExperimentResponse {

    private String id;
    private String tenantId; 
    private String name;
    private String description;
    private ExperimentStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String objective;
    private String methodology;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    //Referências desnormalizadas
    private String laboratoryId;
    private String laboratoryName;
    private String createdBy;
    private String createdByName;


    //Estatísticas
    private Integer totalSamples;

}
