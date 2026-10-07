package com.labcloud.result.mappers;

import com.labcloud.result.dto.request.ResultRequest;
import com.labcloud.result.dto.response.ResultResponse;
import com.labcloud.result.models.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResultMapper - Testes Unitários")
class ResultMapperTest {

    private ResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ResultMapper();
    }

    @Test
    @DisplayName("Deve converter Request para Entity corretamente")
    void shouldConvertRequestToEntity() {
        ResultRequest request = new ResultRequest();
        request.setParameter("Concentração de DNA");
        request.setValue("150.5");
        request.setUnit("ng/µL");
        request.setInstrument("Nanodrop");
        request.setMethod("Espectrofotometria");
        request.setObservations("Boa qualidade");
        request.setIsValid(true);
        request.setQualityControl("Aprovado");
        request.setSampleId("sample-123");

        Result result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getParameter()).isEqualTo("Concentração de DNA");
        assertThat(result.getValue()).isEqualTo("150.5");
        assertThat(result.getUnit()).isEqualTo("ng/µL");
        assertThat(result.getSampleId()).isEqualTo("sample-123");
        assertThat(result.getIsValid()).isTrue();
        assertThat(result.getId()).isNull();
    }

    @Test
    @DisplayName("Deve usar isValid=true como padrão quando null")
    void shouldUseDefaultIsValidWhenNull() {
        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId("sample-123");
        // isValid está null

        Result result = mapper.toEntity(request);

        assertThat(result.getIsValid()).isTrue();
    }

    @Test
    @DisplayName("Deve converter Entity para Response corretamente")
    void shouldConvertEntityToResponse() {
        Result result = Result.builder()
                .parameter("Concentração de DNA")
                .value("150.5")
                .unit("ng/µL")
                .tenantId("lab-bio")
                .sampleId("sample-123")
                .sampleName("Amostra A")
                .experimentId("exp-456")
                .experimentName("Análise de DNA")
                .createdBy("user-789")
                .createdByName("Mauro Silva")
                .isValid(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ResultResponse response = mapper.toResponse(result);

        assertThat(response).isNotNull();
        assertThat(response.getParameter()).isEqualTo("Concentração de DNA");
        assertThat(response.getSampleName()).isEqualTo("Amostra A");
        assertThat(response.getExperimentName()).isEqualTo("Análise de DNA");
        assertThat(response.getCreatedByName()).isEqualTo("Mauro Silva");
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
        Result result = Result.builder()
                .parameter("pH")
                .value("7.0")
                .unit("pH")
                .build();

        ResultRequest request = new ResultRequest();
        request.setValue("7.2");
        // parameter não veio

        mapper.updateEntity(request, result);

        assertThat(result.getParameter()).isEqualTo("pH");   // Não mudou
        assertThat(result.getValue()).isEqualTo("7.2");      // Mudou
    }
}