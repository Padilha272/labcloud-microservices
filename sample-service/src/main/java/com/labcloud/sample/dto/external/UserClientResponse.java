package com.labcloud.sample.dto.external;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa a resposta do Auth Service
 * quando buscamos um usuário via Feign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClientResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private String tenantId;
    private Boolean active;
}
