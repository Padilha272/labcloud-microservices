package com.labcloud.result.services;

import com.labcloud.result.clients.AuthClient;
import com.labcloud.result.clients.SampleClient;
import com.labcloud.result.dto.external.SampleClientResponse;
import com.labcloud.result.dto.external.UserClientResponse;
import com.labcloud.result.dto.request.ResultRequest;
import com.labcloud.result.dto.response.ResultResponse;
import com.labcloud.result.exception.ResourceNotFoundException;
import com.labcloud.result.mappers.ResultMapper;
import com.labcloud.result.models.Result;
import com.labcloud.result.repositories.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultService - Testes Unitários")
class ResultServiceTest {

    // ====== MOCKS ======
    @Mock
    private ResultRepository resultRepository;

    @Mock
    private ResultMapper resultMapper;

    @Mock
    private AuthClient authClient;

    @Mock
    private SampleClient sampleClient;

    // ====== CLASSE TESTADA ======
    @InjectMocks
    private ResultService resultService;

    // ====== DADOS DE TESTE ======
    private Result testResult;
    private ResultRequest testRequest;
    private ResultResponse testResponse;
    private SampleClientResponse testSample;
    private UserClientResponse testUser;

    @BeforeEach
    void setUp() {
        // Sample mock (retornado pelo Sample Service via Feign)
        testSample = SampleClientResponse.builder().id("sample-123").name("Amostra de Sangue A").type("BLOOD")
                .tenantId("lab-bio").experimentId("exp-456").experimentName("Análise de DNA").createdBy("user-789")
                .createdByName("Mauro Silva").build();

        // User mock (retornado pelo Auth Service via Feign)
        testUser = UserClientResponse.builder().id("user-789").name("Mauro Silva").email("mauro@labcloud.com")
                .role("ADMIN").tenantId("lab-bio").active(true).build();

        // Result entity
        testResult = Result.builder().id("result-123").parameter("Concentração de DNA").value("150.5").unit("ng/µL")
                .sampleId("sample-123").sampleName("Amostra de Sangue A").experimentId("exp-456")
                .experimentName("Análise de DNA").createdBy("user-789").createdByName("Mauro Silva").isValid(true)
                .build();
        testResult.updateTenantId("lab-bio");

        // ResultRequest
        testRequest = new ResultRequest();
        testRequest.setParameter("Concentração de DNA");
        testRequest.setValue("150.5");
        testRequest.setUnit("ng/µL");
        testRequest.setSampleId("sample-123");
        testRequest.setIsValid(true);

        // ResultResponse
        testResponse = ResultResponse.builder().id("result-123").parameter("Concentração de DNA").value("150.5")
                .unit("ng/µL").sampleId("sample-123").sampleName("Amostra de Sangue A").experimentId("exp-456")
                .experimentName("Análise de DNA").createdBy("user-789").createdByName("Mauro Silva").build();
    }

    // ============================================================
    // TESTES DE CREATE
    // ============================================================

    @Test
    @DisplayName("Deve criar resultado com sucesso (chamando Sample + Auth via Feign)")
    void shouldCreateResultSuccessfully() {
        // Arrange
        when(sampleClient.getSample("sample-123")).thenReturn(testSample);
        when(authClient.getUser("user-789")).thenReturn(testUser);
        when(resultMapper.toEntity(testRequest)).thenReturn(testResult);
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(resultMapper.toResponse(testResult)).thenReturn(testResponse);

        // Act
        ResultResponse response = resultService.create(testRequest, "user-789");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getParameter()).isEqualTo("Concentração de DNA");
        assertThat(response.getSampleName()).isEqualTo("Amostra de Sangue A");
        assertThat(response.getExperimentName()).isEqualTo("Análise de DNA");
        assertThat(response.getCreatedByName()).isEqualTo("Mauro Silva");

        // Verify — as 2 chamadas Feign foram feitas!
        verify(sampleClient, times(1)).getSample("sample-123");
        verify(authClient, times(1)).getUser("user-789");
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando amostra não existe")
    void shouldThrowExceptionWhenSampleNotFound() {
        // Arrange
        when(sampleClient.getSample("sample-123")).thenThrow(new RuntimeException("Sample not found"));

        // Act & Assert
        assertThatThrownBy(() -> resultService.create(testRequest, "user-789"))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("sample-123");

        // Não deve chamar o Auth nem salvar
        verify(authClient, never()).getUser(anyString());
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(sampleClient.getSample("sample-123")).thenReturn(testSample);
        when(authClient.getUser("user-789")).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThatThrownBy(() -> resultService.create(testRequest, "user-789"))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("user-789");

        verify(resultRepository, never()).save(any(Result.class));
    }

    // ============================================================
    // TESTES DE FIND
    // ============================================================

    @Test
    @DisplayName("Deve buscar resultado por ID")
    void shouldFindResultById() {
        // Arrange
        when(resultRepository.findById("result-123")).thenReturn(Optional.of(testResult));
        when(resultMapper.toResponse(testResult)).thenReturn(testResponse);

        // Act
        ResultResponse response = resultService.findById("result-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("result-123");
        assertThat(response.getParameter()).isEqualTo("Concentração de DNA");
    }

    @Test
    @DisplayName("Deve lançar exceção quando resultado não existe")
    void shouldThrowExceptionWhenResultNotFound() {
        // Arrange
        when(resultRepository.findById("result-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> resultService.findById("result-999")).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("result-999");
    }

    // ============================================================
    // TESTES DE UPDATE
    // ============================================================

    @Test
    @DisplayName("Deve atualizar resultado sem mudar sampleId")
    void shouldUpdateResultWithoutChangingSample() {
        // Arrange
        when(resultRepository.findById("result-123")).thenReturn(Optional.of(testResult));
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(resultMapper.toResponse(testResult)).thenReturn(testResponse);

        // Act
        resultService.update("result-123", testRequest);

        // Assert — não deve chamar Feign se o sampleId não mudou
        verify(sampleClient, never()).getSample(anyString());
        verify(resultMapper, times(1)).updateEntity(testRequest, testResult);
        verify(resultRepository, times(1)).save(testResult);
    }

    @Test
    @DisplayName("Deve atualizar resultado mudando sampleId")
    void shouldUpdateResultChangingSample() {
        // Arrange
        ResultRequest newRequest = new ResultRequest();
        newRequest.setParameter("Novo pH");
        newRequest.setValue("7.2");
        newRequest.setSampleId("sample-nova"); // ← Mudou!

        SampleClientResponse newSample = SampleClientResponse.builder().id("sample-nova").name("Nova Amostra")
                .tenantId("lab-bio").experimentId("exp-999").experimentName("Novo Experimento").build();

        when(resultRepository.findById("result-123")).thenReturn(Optional.of(testResult));
        when(sampleClient.getSample("sample-nova")).thenReturn(newSample);
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(resultMapper.toResponse(testResult)).thenReturn(testResponse);

        // Act
        resultService.update("result-123", newRequest);

        // Assert — deve chamar o Feign porque o sampleId mudou!
        verify(sampleClient, times(1)).getSample("sample-nova");
        verify(resultRepository, times(1)).save(testResult);
    }

    // ============================================================
    // TESTES DE DELETE
    // ============================================================

    @Test
    @DisplayName("Deve deletar resultado")
    void shouldDeleteResult() {
        // Arrange
        when(resultRepository.existsById("result-123")).thenReturn(true);

        // Act
        resultService.delete("result-123");

        // Assert
        verify(resultRepository, times(1)).deleteById("result-123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar resultado inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentResult() {
        // Arrange
        when(resultRepository.existsById("result-999")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resultService.delete("result-999")).isInstanceOf(ResourceNotFoundException.class);

        verify(resultRepository, never()).deleteById(anyString());
    }
}