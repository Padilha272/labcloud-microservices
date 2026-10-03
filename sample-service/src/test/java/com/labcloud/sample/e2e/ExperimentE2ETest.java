package com.labcloud.sample.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labcloud.sample.clients.AuthClient;
import com.labcloud.sample.dto.external.LaboratoryClientResponse;
import com.labcloud.sample.dto.external.UserClientResponse;
import com.labcloud.sample.dto.request.ExperimentRequest;
import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Experiment E2E - Fluxo Completo")
class ExperimentE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthClient authClient;

    private static final String TENANT_ID = "lab-biotech";
    private static final String USER_ID = "user-123";
    private static final String LAB_ID = "lab-123";
    private static final String USER_EMAIL = "joao@lab.com";

    @Test
    @DisplayName("Deve criar experimento com sucesso (fluxo completo)")
    void shouldCreateExperiment() throws Exception {
        
        // 1. Mockar respostas do Auth Service
        LaboratoryClientResponse lab = LaboratoryClientResponse.builder()
                .id(LAB_ID)
                .name("Lab Biotech")
                .tenantId(TENANT_ID)
                .active(true)
                .build();

        UserClientResponse user = UserClientResponse.builder()
                .id(USER_ID)
                .name("João Silva")
                .email(USER_EMAIL)
                .role("ADMIN")
                .tenantId(TENANT_ID)
                .active(true)
                .build();

        when(authClient.getLaboratory(LAB_ID)).thenReturn(lab);
        when(authClient.getUser(USER_ID)).thenReturn(user);

        // 2. Gerar token JWT
        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        // 3. Preparar request
        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setDescription("Extração e análise");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setLaboratoryId(LAB_ID);

        // 4. Executar POST /api/experiments
        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Análise de DNA"))
                .andExpect(jsonPath("$.status").value("PLANNED"))
                .andExpect(jsonPath("$.tenantId").value(TENANT_ID))
                .andExpect(jsonPath("$.laboratoryId").value(LAB_ID))
                .andExpect(jsonPath("$.laboratoryName").value("Lab Biotech"))
                .andExpect(jsonPath("$.createdBy").value(USER_ID))
                .andExpect(jsonPath("$.createdByName").value("João Silva"));

        System.out.println("ETAPA 1: Criar experimento OK");
    }

    @Test
    @DisplayName("Deve listar experimentos do tenant")
    void shouldListExperimentsByTenant() throws Exception {
        // 1. Mockar Auth Service
        when(authClient.getLaboratory(anyString())).thenReturn(
                LaboratoryClientResponse.builder()
                        .id(LAB_ID).name("Lab Biotech").tenantId(TENANT_ID).active(true).build()
        );
        when(authClient.getUser(anyString())).thenReturn(
                UserClientResponse.builder()
                        .id(USER_ID).name("João Silva").email(USER_EMAIL).role("ADMIN").tenantId(TENANT_ID).active(true).build()
        );

        // 2. Criar experimento primeiro
        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setLaboratoryId(LAB_ID);

        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 3. Listar experimentos do tenant
        mockMvc.perform(get("/api/experiments/tenant/" + TENANT_ID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Análise de DNA"))
                .andExpect(jsonPath("$[0].tenantId").value(TENANT_ID));

        System.out.println("ETAPA 2: Listar experimentos OK");
    }

    @Test
    @DisplayName("Não deve criar experimento sem token")
    void shouldNotCreateExperimentWithoutToken() throws Exception {
        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setLaboratoryId(LAB_ID);

        mockMvc.perform(post("/api/experiments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());  // 403

        System.out.println("ETAPA 3: Sem token → 403 OK");
    }

    @Test
    @DisplayName("Não deve criar experimento com token inválido")
    void shouldNotCreateExperimentWithInvalidToken() throws Exception {
        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setLaboratoryId(LAB_ID);

        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());  // 403

        System.out.println("ETAPA 4: Token inválido → 403 OK");
    }

    @Test
    @DisplayName("Não deve criar experimento quando laboratório não existe")
    void shouldNotCreateExperimentWhenLabNotFound() throws Exception {
        // Mockar Auth Service lançando exceção
        when(authClient.getLaboratory(LAB_ID))
                .thenThrow(new RuntimeException("Not found"));

        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ExperimentRequest request = new ExperimentRequest();
        request.setName("Análise de DNA");
        request.setStatus(ExperimentStatus.PLANNED);
        request.setLaboratoryId(LAB_ID);

        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());  // 404

        System.out.println("ETAPA 5: Lab não existe → 404 OK");
    }
}