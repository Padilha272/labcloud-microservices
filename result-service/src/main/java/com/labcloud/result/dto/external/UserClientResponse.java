package com.labcloud.result.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response do Auth Service quando buscamos usuário

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClientResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private String tenantId;
    private String active;

}
