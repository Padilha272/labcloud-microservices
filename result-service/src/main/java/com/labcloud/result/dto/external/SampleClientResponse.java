package com.labcloud.result.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleClientResponse {

    private String id;
    private String name;
    private String type;
    private String tenantId;

    // Dados do Experimento (desnormalizados no Sample)
    private String experimentId;
    private String experimentName;

    // Dados do Criador (desnormalizados no Sample)
    private String createdBy;
    private String createdByName;

}
