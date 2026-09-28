package com.labcloud.sample.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa a resposta do Auth Service
 * quando buscamos um laboratório via Feign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryClientResponse {

    private String id;
    private String name;
    private String tenantId;
    private Boolean active;


}
