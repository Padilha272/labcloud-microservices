package com.labcloud.result.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    private String id;

    // Dados do resultado
    private String parameter;
    private String value;
    private String unit;
    private LocalDateTime measurementDate;
    private String instrument;
    private String method;
    private String observations;
    private Boolean isValid;
    private String qualityControl;

    // Metadados
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Referências desnormalizadas (Sample Service)
    private String sampleId;
    private String sampleName;
    private String experimentId;
    private String experimentName;

    // Referências desnormalizadas (Auth Service)
    private String createdBy;
    private String createdByName;
}
