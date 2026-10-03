package com.labcloud.sample.mappers;

import com.labcloud.sample.dto.request.ExperimentRequest;
import com.labcloud.sample.dto.response.ExperimentResponse;
import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.models.Experiment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExperimentMapper - Testes Unitários")
class ExperimentMapperTest {

    private ExperimentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExperimentMapper();
    }

    @Test
    @DisplayName("Deve converter Request para Entity corretamente")
    void shouldConvertRequestToEntity() {
        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setDescription("Extração e análise");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setObjective("Identificar sequências");
        request.setMethodology("PCR");
        request.setLaboratoryId("lab-123");

        Experiment result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Análise de DNA");
        assertThat(result.getDescription()).isEqualTo("Extração e análise");
        assertThat(result.getStatus()).isEqualTo(ExperimentStatus.PLANNED);
        assertThat(result.getObjective()).isEqualTo("Identificar sequências");
        assertThat(result.getMethodology()).isEqualTo("PCR");
        assertThat(result.getLaboratoryId()).isEqualTo("lab-123");
        assertThat(result.getId()).isNull();
    }

    @Test
    @DisplayName("Deve usar PLANNED como status padrão quando null")
    void shouldUseDefaultStatusWhenNull() {
        ExperimentRequest request = new ExperimentRequest();
        request.setName("Experimento sem status");
        request.setLaboratoryId("lab-123");

        Experiment result = mapper.toEntity(request);

        assertThat(result.getStatus()).isEqualTo(ExperimentStatus.PLANNED);
    }

    @Test
    @DisplayName("Deve converter Entity para Response corretamente")
    void shouldConvertEntityToResponse() {
        Experiment experiment = Experiment.builder()
                .name("Análise de DNA")
                .description("Descrição")
                .status(ExperimentStatus.ACTIVE)
                .objective("Objetivo")
                .methodology("Metodologia")
                .tenantId("lab-bio")
                .laboratoryId("lab-123")
                .laboratoryName("Lab Biotech")
                .createdBy("user-123")
                .createdByName("João Silva")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExperimentResponse response = mapper.toResponse(experiment);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Análise de DNA");
        assertThat(response.getStatus()).isEqualTo(ExperimentStatus.ACTIVE);
        assertThat(response.getTenantId()).isEqualTo("lab-bio");
        assertThat(response.getLaboratoryId()).isEqualTo("lab-123");
        assertThat(response.getLaboratoryName()).isEqualTo("Lab Biotech");
        assertThat(response.getCreatedBy()).isEqualTo("user-123");
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
        Experiment experiment = Experiment.builder()
                .name("Nome Antigo")
                .description("Desc Antiga")
                .status(ExperimentStatus.PLANNED)
                .build();

        ExperimentRequest request = new ExperimentRequest();
        request.setName("Nome Novo");
        request.setStatus(ExperimentStatus.ACTIVE);

        mapper.updateEntity(request, experiment);

        assertThat(experiment.getName()).isEqualTo("Nome Novo");
        assertThat(experiment.getStatus()).isEqualTo(ExperimentStatus.ACTIVE);
        assertThat(experiment.getDescription()).isEqualTo("Desc Antiga");  // Não mudou
    }
}