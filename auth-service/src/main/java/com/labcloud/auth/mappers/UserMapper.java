package com.labcloud.auth.mappers;

import org.springframework.stereotype.Component;

import com.labcloud.auth.dto.request.UserRequest;
import com.labcloud.auth.dto.response.UserResponse;
import com.labcloud.auth.models.User;


@Component 
public class UserMapper {

    //Converter de Request para entity
    public User toEntity(UserRequest request){
        if (request ==null){
            return null;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());//Será criptografado
        user.setRole(request.getRole());
        return user;
    }


    public UserResponse toResponse(User user){
        if (user ==null){
            return null;
        }


    return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .tenantId(user.getTenantId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .laboratoryId(user.getLaboratory() != null ? user.getLaboratory().getId() : null)
                .laboratoryName(user.getLaboratory() != null ? user.getLaboratory().getName() : null)
                .build();




    }

    public void updateEntity (UserRequest request, User user) {
        if(request == null || user == null){
            return ;
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if(user.getRole() != null){
            user.setRole(request.getRole());
        }
        

    }
    
}
