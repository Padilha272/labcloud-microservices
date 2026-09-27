package com.labcloud.auth.mappers;

import com.labcloud.auth.dto.request.UserRequest;
import com.labcloud.auth.dto.response.UserResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@DisplayName("UserMapper - Testes unitários")
public class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach 
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    @DisplayName("Deve converter Request para Entity corretamente")
    void shouldConvertRequestToEntity() {
        UserRequest request = new UserRequest();
        request.setName("João Silva");
        request.setEmail("joao@lab.com");
        request.setPassword("senha123");
        request.setRole(UserRole.ADMIN);
        request.setLaboratoryId("lab-id-123");

        User result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@lab.com");
        assertThat(result.getPassword()).isEqualTo("senha123");
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(result.getId()).isNull();
    }

     @Test
    @DisplayName("Deve converter Entity para Response corretamente")
    void shouldConvertEntityToResponse() {
        Laboratory lab = Laboratory.builder()
                .name("Lab Test")
                .build();
        lab.updateTenantId("lab-test");

        User user = User.builder()
                .name("João Silva")
                .email("joao@lab.com")
                .password("hashed")
                .role(UserRole.ADMIN)
                .active(true)
                .laboratory(lab)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        user.updateTenantId("lab-test");

        UserResponse response = mapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@lab.com");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getTenantId()).isEqualTo("lab-test");
        assertThat(response.getLaboratoryId()).isEqualTo(lab.getId());
        assertThat(response.getLaboratoryName()).isEqualTo("Lab Test"); 
    }

    @Test
    @DisplayName("Deve retornar null para Request null")
    void shouldReturnNullForNullRequest() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Deve retornar null para Entity null")
    void shouldReturnNullForNullEntity() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Deve atualizar apenas campos não-nulos")
    void shouldUpdateOnlyNotNullFields() {
        User user = User.builder()
                .name("Nome Antigo")
                .email("antigo@lab.com")
                .role(UserRole.RESEARCHER)
                .build();

        UserRequest request = new UserRequest();
        request.setName("Nome Novo");
        request.setRole(UserRole.ADMIN);

        mapper.updateEntity(request, user);

        assertThat(user.getName()).isEqualTo("Nome Novo");
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getEmail()).isEqualTo("antigo@lab.com");  // Não mudou
    }
}
