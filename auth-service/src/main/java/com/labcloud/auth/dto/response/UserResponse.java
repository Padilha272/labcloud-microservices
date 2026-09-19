package com.labcloud.auth.dto.response;

import java.time.LocalDateTime;

import com.labcloud.auth.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor 
public class UserResponse {
    private String id;
    private String tenantId;
    private String name;
    private String email;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    //Dados do laboratório
    private String laboratoryId;
    private String laboratoryName;
}

