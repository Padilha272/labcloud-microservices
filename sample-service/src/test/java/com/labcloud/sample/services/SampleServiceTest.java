package com.labcloud.sample.services;

import com.labcloud.sample.clients.AuthClient;
import com.labcloud.sample.dto.external.UserClientResponse;
import com.labcloud.sample.dto.request.SampleRequest;
import com.labcloud.sample.dto.response.SampleResponse;
import com.labcloud.sample.exception.ResourceNotFoundException;
import com.labcloud.sample.mappers.SampleMapper;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.models.Sample;
import com.labcloud.sample.repositories.ExperimentRepository;
import com.labcloud.sample.repositories.SampleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SampleService - Testes Unitários")
class SampleServiceTest {

    //Mocks
    @Mock
    private SampleRepository sampleRepository;

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private AuthClient authClient;

    //Classe testada
    @InjectMocks
    private SampleService sampleService;

    //Dados de teste
    private Sample testSample;
    private SampleRequest testRequest;
    private SampleResponse testResponse;
    private Experiment testExperiment;
    private UserClientResponse testUser;

    @BeforeEach
    void setUp() {
        testExperiment = Experiment.builder()
                .id("exp-123")
                .name("Análise de DNA")
                .status(com.labcloud.sample.enums.ExperimentStatus.ACTIVE)
                .laboratoryId("lab-123")
                .laboratoryName("Lab Biotech")
                .createdBy("user-123")
                .createdByName("João Silva")
                .build();
        testExperiment.updateTenantId("lab-biotech");

        testUser = UserClientResponse.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao@lab.com")
                .role("ADMIN")
                .tenantId("lab-biotech")
                .active(true)
                .build();

        testSample = Sample.builder()
                .name("Amostra de Sangue")
                .type("BLOOD")
                .quantity(5.0)
                .unit("mL")
                .experiment(testExperiment)
                .createdBy("user-123")
                .createdByName("João Silva")
                .build();
        testSample.updateTenantId("lab-biotech");

        testRequest = new SampleRequest();
        testRequest.setName("Amostra de Sangue");
        testRequest.setType("BLOOD");
        testRequest.setQuantity(5.0);
        testRequest.setUnit("mL");
        testRequest.setExperimentId("exp-123");

        testResponse = SampleResponse.builder()
                .id("sample-123")
                .name("Amostra de Sangue")
                .type("BLOOD")
                .experimentId("exp-123")
                .experimentName("Análise de DNA")
                .createdByName("João Silva")
                .build();
    }

    //Testes de create

    @Test
    @DisplayName("Deve criar amostra com sucesso")
    void shouldCreateSampleSuccessfully() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(authClient.getUser("user-123")).thenReturn(testUser);
        when(sampleMapper.toEntity(testRequest)).thenReturn(testSample);
        when(sampleRepository.save(any(Sample.class))).thenReturn(testSample);
        when(sampleMapper.toResponse(testSample)).thenReturn(testResponse);

        // Act
        SampleResponse response = sampleService.create(testRequest, "user-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Amostra de Sangue");
        assertThat(response.getExperimentName()).isEqualTo("Análise de DNA");
        assertThat(response.getCreatedByName()).isEqualTo("João Silva");

        // Verify
        verify(experimentRepository, times(1)).findById("exp-123");
        verify(authClient, times(1)).getUser("user-123");
        verify(sampleRepository, times(1)).save(any(Sample.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando experimento não existe")
    void shouldThrowExceptionWhenExperimentNotFound() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sampleService.create(testRequest, "user-123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("exp-123");

        verify(authClient, never()).getUser(anyString());
        verify(sampleRepository, never()).save(any(Sample.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(authClient.getUser("user-123"))
                .thenThrow(new RuntimeException("Not found"));

        // Act & Assert
        assertThatThrownBy(() -> sampleService.create(testRequest, "user-123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("user-123");

        verify(sampleRepository, never()).save(any(Sample.class));
    }

    //Teste de find
    @Test
    @DisplayName("Deve buscar amostra por ID")
    void shouldFindSampleById() {
        // Arrange
        when(sampleRepository.findById("sample-123")).thenReturn(Optional.of(testSample));
        when(sampleMapper.toResponse(testSample)).thenReturn(testResponse);

        // Act
        SampleResponse response = sampleService.findById("sample-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Amostra de Sangue");
    }

    @Test
    @DisplayName("Deve lançar exceção quando amostra não existe")
    void shouldThrowExceptionWhenSampleNotFound() {
        // Arrange
        when(sampleRepository.findById("sample-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sampleService.findById("sample-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("sample-999");
    }

    //Testes de update
    @Test
    @DisplayName("Deve atualizar amostra com sucesso")
    void shouldUpdateSample() {
        // Arrange
        when(sampleRepository.findById("sample-123")).thenReturn(Optional.of(testSample));
        when(sampleRepository.save(any(Sample.class))).thenReturn(testSample);
        when(sampleMapper.toResponse(testSample)).thenReturn(testResponse);

        // Act
        sampleService.update("sample-123", testRequest);

        // Assert
        verify(sampleMapper, times(1)).updateEntity(testRequest, testSample);
        verify(sampleRepository, times(1)).save(testSample);
    }

    //Teste de delete
    @Test
    @DisplayName("Deve deletar amostra")
    void shouldDeleteSample() {
        // Arrange
        when(sampleRepository.existsById("sample-123")).thenReturn(true);

        // Act
        sampleService.delete("sample-123");

        // Assert
        verify(sampleRepository, times(1)).deleteById("sample-123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar amostra inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentSample() {
        // Arrange
        when(sampleRepository.existsById("sample-999")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> sampleService.delete("sample-999"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(sampleRepository, never()).deleteById(any());
    }
}