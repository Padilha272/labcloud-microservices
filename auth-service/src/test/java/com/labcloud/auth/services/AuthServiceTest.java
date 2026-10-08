package com.labcloud.auth.services;

import com.labcloud.auth.dto.request.AuthRequest;
import com.labcloud.auth.dto.request.RegisterRequest;
import com.labcloud.auth.dto.response.AuthResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.exception.DuplicateResourceException;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.models.User;
import com.labcloud.auth.repositories.LaboratoryRepository;
import com.labcloud.auth.repositories.UserRepository;
import com.labcloud.common.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
public class AuthServiceTest {

    
    // ====== MOCKS ======
    @Mock
    private UserRepository userRepository;

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    // ====== CLASSE TESTADA ======
    @InjectMocks
    private AuthService authService;

    // ====== DADOS DE TESTE ======
    private User testUser;
    private Laboratory testLab;

    @BeforeEach
    void setUp() {
        testLab = Laboratory.builder()
                .name("Laboratório de Biotecnologia")
                .active(true)
                .build();
        testLab.updateTenantId("laboratorio-de-biotecnologia");

        testUser = User.builder()
                .name("João Silva")
                .email("joao@labcloud.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .active(true)
                .laboratory(testLab)
                .build();
        testUser.updateTenantId("laboratorio-de-biotecnologia");
    }

    
    // TESTES DE LOGIN

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void shouldLoginSuccessfully() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("joao@labcloud.com");
        request.setPassword("senha123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmailWithLaboratory("joao@labcloud.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(nullable(String.class), anyString(), anyString(), anyString()))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("joao@labcloud.com");
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getTenantId()).isEqualTo("laboratorio-de-biotecnologia");
        assertThat(response.getLaboratoryName()).isEqualTo("Laboratório de Biotecnologia");

        // Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmailWithLaboratory("joao@labcloud.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(nullable(String.class), anyString(), anyString(), anyString());
    }

    
    // TESTES DE REGISTRO
    
    @Test
    @DisplayName("Não deve registrar com email duplicado")
    void shouldNotRegisterWithDuplicateEmail() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("João Silva");
        request.setEmail("joao@labcloud.com");
        request.setPassword("senha123");
        request.setLaboratoryName("Laboratório de Biotecnologia");

        when(userRepository.existsByEmail("joao@labcloud.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("joao@labcloud.com");

        verify(userRepository, never()).save(any(User.class));
        verify(laboratoryRepository, never()).save(any(Laboratory.class));
    }

    @Test
    @DisplayName("Deve registrar novo laboratório e admin com sucesso")
    void shouldRegisterSuccessfully() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("João Silva");
        request.setEmail("joao@labcloud.com");
        request.setPassword("senha123");
        request.setLaboratoryName("Laboratório de Biotecnologia");

        when(userRepository.existsByEmail("joao@labcloud.com")).thenReturn(false);
        when(laboratoryRepository.existsByTenantId(anyString())).thenReturn(false);
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(testLab);
        when(passwordEncoder.encode("senha123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(nullable(String.class), anyString(), anyString(), anyString()))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("joao@labcloud.com");
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getTenantId()).isEqualTo("laboratorio-de-biotecnologia");

        // Verify
        verify(userRepository, times(1)).existsByEmail("joao@labcloud.com");
        verify(laboratoryRepository, times(1)).save(any(Laboratory.class));
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(nullable(String.class), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve gerar tenantId correto a partir do nome do laboratório")
    void shouldGenerateCorrectTenantId() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("João Silva");
        request.setEmail("joao@labcloud.com");
        request.setPassword("senha123");
        request.setLaboratoryName("Laboratório de Biotecnologia");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(laboratoryRepository.existsByTenantId(anyString())).thenReturn(false);
        when(laboratoryRepository.save(any(Laboratory.class))).thenAnswer(invocation -> {
            Laboratory lab = invocation.getArgument(0);
            assertThat(lab.getTenantId()).isEqualTo("laboratorio-de-biotecnologia");
            return lab;
        });
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(nullable(String.class), anyString(), anyString(), anyString()))
                .thenReturn("fake-jwt-token");

        // Act
        authService.register(request);

        // Assert
        verify(laboratoryRepository, times(1)).save(any(Laboratory.class));
    }
    
}
