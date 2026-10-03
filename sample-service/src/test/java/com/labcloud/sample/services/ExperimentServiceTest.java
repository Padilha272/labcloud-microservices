package com.labcloud.sample.services;

import com.labcloud.sample.clients.AuthClient;
import com.labcloud.sample.dto.external.LaboratoryClientResponse;
import com.labcloud.sample.dto.external.UserClientResponse;
import com.labcloud.sample.dto.request.ExperimentRequest;
import com.labcloud.sample.dto.response.ExperimentResponse;
import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.exception.ResourceNotFoundException;
import com.labcloud.sample.mappers.ExperimentMapper;
import com.labcloud.sample.models.Experiment;
import com.labcloud.sample.repositories.ExperimentRepository;
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
@DisplayName("ExperimentService - Testes Unitários")
class ExperimentServiceTest {

    //Mocks
    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentMapper experimentMapper;

    @Mock
    private AuthClient authClient;   //Mock do Feign

    //Classe testada
    @InjectMocks
    private ExperimentService experimentService;

    //Dados do teste
    private Experiment testExperiment;
    private ExperimentRequest testRequest;
    private ExperimentResponse testResponse;
    private LaboratoryClientResponse testLab;
    private UserClientResponse testUser;

    @BeforeEach
    void setUp() {
        testLab = LaboratoryClientResponse.builder()
                .id("lab-123")
                .name("Lab Biotech")
                .tenantId("lab-biotech")
                .active(true)
                .build();

        testUser = UserClientResponse.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao@lab.com")
                .role("ADMIN")
                .tenantId("lab-biotech")
                .active(true)
                .build();

        testExperiment = Experiment.builder()
                .name("Análise de DNA")
                .description("Extração e análise")
                .status(ExperimentStatus.PLANNED)
                .laboratoryId("lab-123")
                .laboratoryName("Lab Biotech")
                .createdBy("user-123")
                .createdByName("João Silva")
                .build();
        testExperiment.updateTenantId("lab-biotech");

        testRequest = new ExperimentRequest();
        testRequest.setName("Análise de DNA");
        testRequest.setDescription("Extração e análise");
        testRequest.setStatus(ExperimentStatus.PLANNED);
        testRequest.setLaboratoryId("lab-123");

        testResponse = ExperimentResponse.builder()
                .id("exp-123")
                .name("Análise de DNA")
                .laboratoryName("Lab Biotech")
                .createdByName("João Silva")
                .build();
    }

    
    //Testes do create
    @Test
    @DisplayName("Deve criar experimento com sucesso")
    void shouldCreateExperimentSuccessfully() {
        // Arrange
        when(authClient.getLaboratory("lab-123")).thenReturn(testLab);
        when(authClient.getUser("user-123")).thenReturn(testUser);
        when(experimentMapper.toEntity(testRequest)).thenReturn(testExperiment);
        when(experimentRepository.save(any(Experiment.class))).thenReturn(testExperiment);
        when(experimentMapper.toResponse(testExperiment)).thenReturn(testResponse);

        // Act
        ExperimentResponse response = experimentService.create(testRequest, "user-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Análise de DNA");
        assertThat(response.getLaboratoryName()).isEqualTo("Lab Biotech");
        assertThat(response.getCreatedByName()).isEqualTo("João Silva");

        // Verify
        verify(authClient, times(1)).getLaboratory("lab-123");
        verify(authClient, times(1)).getUser("user-123");
        verify(experimentRepository, times(1)).save(any(Experiment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando laboratório não existe")
    void shouldThrowExceptionWhenLaboratoryNotFound() {
        // Arrange
        when(authClient.getLaboratory("lab-123"))
                .thenThrow(new RuntimeException("Not found"));

        // Act & Assert
        assertThatThrownBy(() -> experimentService.create(testRequest, "user-123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("lab-123");

        verify(experimentRepository, never()).save(any(Experiment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(authClient.getLaboratory("lab-123")).thenReturn(testLab);
        when(authClient.getUser("user-123"))
                .thenThrow(new RuntimeException("Not found"));

        // Act & Assert
        assertThatThrownBy(() -> experimentService.create(testRequest, "user-123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("user-123");

        verify(experimentRepository, never()).save(any(Experiment.class));
    }

    //Testes do find
    @Test
    @DisplayName("Deve buscar experimento por ID")
    void shouldFindExperimentById() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(experimentMapper.toResponse(testExperiment)).thenReturn(testResponse);

        // Act
        ExperimentResponse response = experimentService.findById("exp-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Análise de DNA");
    }

    @Test
    @DisplayName("Deve lançar exceção quando experimento não existe")
    void shouldThrowExceptionWhenExperimentNotFound() {
        // Arrange
        when(experimentRepository.findById("exp-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> experimentService.findById("exp-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("exp-999");
    }

    //Testes de ações e negócio
    @Test
    @DisplayName("Deve ativar experimento")
    void shouldActivateExperiment() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(experimentRepository.save(any(Experiment.class))).thenReturn(testExperiment);
        when(experimentMapper.toResponse(testExperiment)).thenReturn(testResponse);

        // Act
        experimentService.activate("exp-123");

        // Assert
        assertThat(testExperiment.getStatus()).isEqualTo(ExperimentStatus.ACTIVE);
        assertThat(testExperiment.getStartDate()).isNotNull();
        verify(experimentRepository, times(1)).save(testExperiment);
    }

    @Test
    @DisplayName("Deve completar experimento")
    void shouldCompleteExperiment() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(experimentRepository.save(any(Experiment.class))).thenReturn(testExperiment);
        when(experimentMapper.toResponse(testExperiment)).thenReturn(testResponse);

        // Act
        experimentService.complete("exp-123");

        // Assert
        assertThat(testExperiment.getStatus()).isEqualTo(ExperimentStatus.COMPLETED);
        assertThat(testExperiment.getEndDate()).isNotNull();
    }

    @Test
    @DisplayName("Deve cancelar experimento")
    void shouldCancelExperiment() {
        // Arrange
        when(experimentRepository.findById("exp-123")).thenReturn(Optional.of(testExperiment));
        when(experimentRepository.save(any(Experiment.class))).thenReturn(testExperiment);
        when(experimentMapper.toResponse(testExperiment)).thenReturn(testResponse);

        // Act
        experimentService.cancel("exp-123");

        // Assert
        assertThat(testExperiment.getStatus()).isEqualTo(ExperimentStatus.CANCELLED);
        assertThat(testExperiment.getEndDate()).isNotNull();
    }

    //Testes de delete
    @Test
    @DisplayName("Deve deletar experimento")
    void shouldDeleteExperiment() {
        // Arrange
        when(experimentRepository.existsById("exp-123")).thenReturn(true);

        // Act
        experimentService.delete("exp-123");

        // Assert
        verify(experimentRepository, times(1)).deleteById("exp-123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar experimento inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentExperiment() {
        // Arrange
        when(experimentRepository.existsById("exp-999")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> experimentService.delete("exp-999"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(experimentRepository, never()).deleteById(any());
    }
}