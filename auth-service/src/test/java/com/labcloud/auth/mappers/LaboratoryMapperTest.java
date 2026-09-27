package com.labcloud.auth.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.labcloud.auth.dto.request.LaboratoryRequest;
import com.labcloud.auth.dto.response.LaboratoryResponse;
import com.labcloud.auth.mappers.LaboratoryMapper;
import com.labcloud.auth.models.Laboratory;

@DisplayName("LaboratoryMapper - Testes Unitários")
public class LaboratoryMapperTest {

    private LaboratoryMapper mapper;

    @BeforeEach 
    void setUp() {
        mapper = new LaboratoryMapper();
    }

    @Test 
    @DisplayName("Deve Converter Request para Entity Corretamente")
    void shouldConvertRequestToEntity() {
        LaboratoryRequest request = new LaboratoryRequest();
        request.setName("Laboratório de Biotecnologia");
        request.setDescription("Descrição do laboratório");
        request.setAddress("Rua A, 123");
        request.setPhone("11999999999");
        request.setEmail("lab@test.com");

        Laboratory result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laboratório de Biotecnologia");
         assertThat(result.getDescription()).isEqualTo("Descrição do laboratório");
        assertThat(result.getAddress()).isEqualTo("Rua A, 123");
        assertThat(result.getPhone()).isEqualTo("11999999999");
        assertThat(result.getEmail()).isEqualTo("lab@test.com");
        assertThat(result.getId()).isNull();
        assertThat(result.getTenantId()).isNull();
    }

    @Test 
    @DisplayName("Deve Converter Entity para response Corretamente")
    void shouldConvertEntityToResponse() {
         Laboratory laboratory = Laboratory.builder()
                .name("Lab Test")
                .description("Desc")
                .address("Rua B")
                .phone("11888888888")
                .email("test@lab.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        laboratory.updateTenantId("lab-test");


        LaboratoryResponse response = mapper.toResponse(laboratory);
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Lab Test");
        assertThat(response.getTenantId()).isEqualTo("lab-test");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getActive()).isTrue();

    }

    @Test 
    @DisplayName("Deve retornar null quando Request for null")
    void shouldReturnNullForNullRequest(){
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test 
    @DisplayName("Deve retornar null quando Entity for null")
    void shouldReturnNullForNullEntity(){
        assertThat(mapper.toResponse(null)).isNull();
    }

     @Test
    @DisplayName("Deve atualizar apenas campos não-nulos")
    void shouldUpdateOnlyNotNullFields() {
        Laboratory laboratory = Laboratory.builder()
                .name("Nome Antigo")
                .description("Desc Antiga")
                .build();

        LaboratoryRequest request = new LaboratoryRequest();
        request.setName("Nome Novo");

        mapper.updateEntity(request, laboratory);

        assertThat(laboratory.getName()).isEqualTo("Nome Novo");
        assertThat(laboratory.getDescription()).isEqualTo("Desc Antiga");
    }
         

}
