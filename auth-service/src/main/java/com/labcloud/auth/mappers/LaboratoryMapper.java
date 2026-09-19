package com.labcloud.auth.mappers;

import org.springframework.stereotype.Component;

import com.labcloud.auth.dto.request.LaboratoryRequest;
import com.labcloud.auth.dto.response.LaboratoryResponse;
import com.labcloud.auth.models.Laboratory;

@Component 
public class LaboratoryMapper {


    //Converter Request em entity
    public Laboratory toEntity(LaboratoryRequest request) {

        if (request ==null) {
            return null;
        }

        Laboratory laboratory = new Laboratory();
        laboratory.setName(request.getName());
        laboratory.setDescription(request.getDescription());
        laboratory.setAddress(request.getAddress());
        laboratory.setPhone(request.getPhone());
        laboratory.setEmail(request.getEmail());

        return laboratory;
    }

    //Converter Entity em response

    public LaboratoryResponse toResponse (Laboratory laboratory){
        if (laboratory == null) {
            return null;
        }

        return LaboratoryResponse.builder()
                .id(laboratory.getId())
                .name(laboratory.getName())
                .tenantId(laboratory.getTenantId())
                .description(laboratory.getDescription())
                .address(laboratory.getAddress())
                .phone(laboratory.getPhone())
                .email(laboratory.getEmail())
                .active(laboratory.getActive())
                .createdAt(laboratory.getCreatedAt())
                .updatedAt(laboratory.getUpdatedAt())
                .totalUsers(laboratory.getUsers() != null ? laboratory.getUsers().size() : 0)
                .build();
    }

}
