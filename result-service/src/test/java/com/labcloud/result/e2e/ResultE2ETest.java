package com.labcloud.result.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labcloud.result.clients.AuthClient;
import com.labcloud.result.clients.SampleClient;
import com.labcloud.result.dto.external.SampleClientResponse;
import com.labcloud.result.dto.external.UserClientResponse;
import com.labcloud.result.dto.request.ResultRequest;

import com.labcloud.common.security.JwtService;
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
@DisplayName("Result E2E - Fluxo Completo")
class ResultE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private SampleClient sampleClient;

    @MockBean
    private AuthClient authClient;

    private static final String TENANT_ID = "lab-biotech";
    private static final String USER_ID = "user-123";
    private static final String SAMPLE_ID = "sample-123";
    private static final String USER_EMAIL = "mauro@labcloud.com";

    @Test
    @DisplayName("Deve criar resultado com sucesso (chamando Sample + Auth)")
    void shouldCreateResultSuccessfully() throws Exception {
        // ==========================================
        // 1. Mockar Sample Service
        // ==========================================
        SampleClientResponse sample = SampleClientResponse.builder().id(SAMPLE_ID).name("Amostra de Sangue A")
                .type("BLOOD").tenantId(TENANT_ID).experimentId("exp-456").experimentName("Análise de DNA")
                .createdBy(USER_ID).createdByName("Mauro Silva").build();

        when(sampleClient.getSample(SAMPLE_ID)).thenReturn(sample);

        // ==========================================
        // 2. Mockar Auth Service
        // ==========================================
        UserClientResponse user = UserClientResponse.builder().id(USER_ID).name("Mauro Silva").email(USER_EMAIL)
                .role("ADMIN").tenantId(TENANT_ID).active(true).build();

        when(authClient.getUser(USER_ID)).thenReturn(user);

        // ==========================================
        // 3. Gerar token JWT
        // ==========================================
        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        // ==========================================
        // 4. Preparar request
        // ==========================================
        ResultRequest request = new ResultRequest();
        request.setParameter("Concentração de DNA");
        request.setValue("150.5");
        request.setUnit("ng/µL");
        request.setInstrument("Nanodrop");
        request.setMethod("Espectrofotometria");
        request.setIsValid(true);
        request.setSampleId(SAMPLE_ID);

        // ==========================================
        // 5. Executar POST /api/results
        // ==========================================
        mockMvc.perform(post("/api/results").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.parameter").value("Concentração de DNA"))
                .andExpect(jsonPath("$.value").value("150.5")).andExpect(jsonPath("$.unit").value("ng/µL"))
                .andExpect(jsonPath("$.tenantId").value(TENANT_ID)).andExpect(jsonPath("$.sampleId").value(SAMPLE_ID))
                .andExpect(jsonPath("$.sampleName").value("Amostra de Sangue A"))
                .andExpect(jsonPath("$.experimentId").value("exp-456"))
                .andExpect(jsonPath("$.experimentName").value("Análise de DNA"))
                .andExpect(jsonPath("$.createdBy").value(USER_ID))
                .andExpect(jsonPath("$.createdByName").value("Mauro Silva"));

        System.out.println("ETAPA 1: Criar resultado OK");
    }

    @Test
    @DisplayName("Deve buscar resultado por ID após criação")
    void shouldFindResultByIdAfterCreation() throws Exception {
        // Criar resultado
        SampleClientResponse sample = SampleClientResponse.builder().id(SAMPLE_ID).name("Amostra A").tenantId(TENANT_ID)
                .experimentId("exp-456").experimentName("Análise DNA").build();

        UserClientResponse user = UserClientResponse.builder().id(USER_ID).name("Mauro Silva").tenantId(TENANT_ID)
                .build();

        when(sampleClient.getSample(SAMPLE_ID)).thenReturn(sample);
        when(authClient.getUser(USER_ID)).thenReturn(user);

        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        MvcResult createResult = mockMvc
                .perform(post("/api/results").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();

        // Extrair ID criado
        String responseBody = createResult.getResponse().getContentAsString();
        String resultId = objectMapper.readTree(responseBody).get("id").asText();

        // Buscar por ID
        mockMvc.perform(get("/api/results/" + resultId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(resultId))
                .andExpect(jsonPath("$.parameter").value("pH"));

        System.out.println("ETAPA 2: Buscar por ID OK");
    }

    @Test
    @DisplayName("Deve listar resultados do tenant")
    void shouldListResultsByTenant() throws Exception {
        // Criar resultado
        SampleClientResponse sample = SampleClientResponse.builder().id(SAMPLE_ID).name("Amostra A").tenantId(TENANT_ID)
                .experimentId("exp-456").experimentName("Análise DNA").build();

        UserClientResponse user = UserClientResponse.builder().id(USER_ID).name("Mauro Silva").tenantId(TENANT_ID)
                .build();

        when(sampleClient.getSample(anyString())).thenReturn(sample);
        when(authClient.getUser(anyString())).thenReturn(user);

        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        mockMvc.perform(post("/api/results").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Listar por tenant
        mockMvc.perform(get("/api/results/tenant/" + TENANT_ID).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].parameter").value("pH"))
                .andExpect(jsonPath("$[0].tenantId").value(TENANT_ID));

        System.out.println("ETAPA 3: Listar por tenant OK");
    }

    @Test
    @DisplayName("Não deve criar resultado sem token")
    void shouldNotCreateResultWithoutToken() throws Exception {
        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        mockMvc.perform(post("/api/results").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isForbidden()); // 403 (sem
                                                                                                       // token)

        System.out.println("ETAPA 4: Sem token → 403 OK");
    }

    @Test
    @DisplayName("Não deve criar resultado com token inválido")
    void shouldNotCreateResultWithInvalidToken() throws Exception {
        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        mockMvc.perform(post("/api/results").header("Authorization", "Bearer token-invalido")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        System.out.println("ETAPA 5: Token inválido → 403 OK");
    }

    @Test
    @DisplayName("Não deve criar resultado quando amostra não existe")
    void shouldNotCreateResultWhenSampleNotFound() throws Exception {
        // Mock: Sample NÃO existe
        when(sampleClient.getSample(SAMPLE_ID)).thenThrow(new RuntimeException("Sample not found"));

        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        mockMvc.perform(post("/api/results").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // 404

        System.out.println("ETAPA 6: Sample não existe → 404 OK");
    }

    @Test
    @DisplayName("Não deve criar resultado quando usuário não existe")
    void shouldNotCreateResultWhenUserNotFound() throws Exception {
        // Mock: Sample existe, mas User NÃO
        SampleClientResponse sample = SampleClientResponse.builder().id(SAMPLE_ID).name("Amostra A").tenantId(TENANT_ID)
                .build();

        when(sampleClient.getSample(SAMPLE_ID)).thenReturn(sample);
        when(authClient.getUser(USER_ID)).thenThrow(new RuntimeException("User not found"));

        String token = jwtService.generateToken(USER_ID, USER_EMAIL, TENANT_ID, "ADMIN");

        ResultRequest request = new ResultRequest();
        request.setParameter("pH");
        request.setValue("7.2");
        request.setSampleId(SAMPLE_ID);

        mockMvc.perform(post("/api/results").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // 404

        System.out.println("ETAPA 7: User não existe → 404 OK");
    }
}
