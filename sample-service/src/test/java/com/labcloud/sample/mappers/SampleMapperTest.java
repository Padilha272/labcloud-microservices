package com.labcloud.sample.mappers;

import com.labcloud.sample.dto.request.SampleRequest;
import com.labcloud.sample.dto.response.SampleResponse;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.models.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SampleMapper - Testes Unitários")
class SampleMapperTest {

    private SampleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SampleMapper();
    }

    @Test
    @DisplayName("Deve converter Request para Entity corretamente")
    void shouldConvertRequestToEntity() {
        SampleRequest request = new SampleRequest();
        request.setName("Amostra de Sangue");
        request.setType("BLOOD");
        request.setCollectionMethod("Punção");
        request.setQuantity(5.0);
        request.setUnit("mL");
        request.setStorageConditions("-80°C");
        request.setLocation("Freezer A1");
        request.setNotes("Amostra fresca");
        request.setExperimentId("exp-123");

        Sample result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Amostra de Sangue");
        assertThat(result.getType()).isEqualTo("BLOOD");
        assertThat(result.getQuantity()).isEqualTo(5.0);
        assertThat(result.getUnit()).isEqualTo("mL");
        assertThat(result.getLocation()).isEqualTo("Freezer A1");
        assertThat(result.getId()).isNull();
    }

    @Test
    @DisplayName("Deve converter Entity para Response corretamente")
    void shouldConvertEntityToResponse() {
        Experiment experiment = Experiment.builder()
                .name("Análise de DNA")
                .build();

        Sample sample = Sample.builder()
                .name("Amostra de Sangue")
                .type("BLOOD")
                .quantity(5.0)
                .unit("mL")
                .tenantId("lab-bio")
                .experiment(experiment)
                .createdBy("user-123")
                .createdByName("João Silva")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SampleResponse response = mapper.toResponse(sample);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Amostra de Sangue");
        assertThat(response.getType()).isEqualTo("BLOOD");
        assertThat(response.getQuantity()).isEqualTo(5.0);
        assertThat(response.getTenantId()).isEqualTo("lab-bio");
        assertThat(response.getExperimentName()).isEqualTo("Análise de DNA");
        assertThat(response.getCreatedByName()).isEqualTo("João Silva");
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
        Sample sample = Sample.builder()
                .name("Nome Antigo")
                .type("BLOOD")
                .quantity(3.0)
                .build();

        SampleRequest request = new SampleRequest();
        request.setName("Nome Novo");
        request.setQuantity(5.0);

        mapper.updateEntity(request, sample);

        assertThat(sample.getName()).isEqualTo("Nome Novo");
        assertThat(sample.getQuantity()).isEqualTo(5.0);
        assertThat(sample.getType()).isEqualTo("BLOOD");  // Não mudou
    }
}