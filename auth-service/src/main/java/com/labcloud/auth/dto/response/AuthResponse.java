package com.labcloud.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class AuthResponse {
    private String token;
    private String userId;
    private String name;
    private String email;
    private String role;
    private String tenantId;
    private String laboratoryName;
}
