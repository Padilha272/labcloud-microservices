package com.labcloud.auth.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labcloud.auth.dto.request.AuthRequest;
import com.labcloud.auth.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth E2E - Fluxo Completo")
public class AuthE2ETest {

     @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar, fazer login e acessar endpoint protegido")
    void shouldRegisterLoginAndAccessProtectedEndpoint() throws Exception {
        
        // ETAPA 1: REGISTRAR novo laboratório + admin
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("João Silva");
        registerRequest.setEmail("joao.e2e@labcloud.com");
        registerRequest.setPassword("senha123");
        registerRequest.setLaboratoryName("Laboratório E2E");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("joao.e2e@labcloud.com"))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.tenantId").value("laboratorio-e2e"))
                .andExpect(jsonPath("$.laboratoryName").value("Laboratório E2E"))
                .andReturn();

        System.out.println("ETAPA 1: Registro OK");

        
        //ETAPA 2: Fazer Login com as credenciais
        
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("joao.e2e@labcloud.com");
        loginRequest.setPassword("senha123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("joao.e2e@labcloud.com"))
                .andReturn();

        System.out.println("ETAPA 2: Login OK");

        
        // ETAPA 3: EXTRAIR token da resposta
        
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        System.out.println("Token extraído: " + token.substring(0, 30) + "...");

        
        // ETAPA 4: ACESSAR endpoint protegido COM token
        
        mockMvc.perform(get("/api/laboratories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tenantId").value("laboratorio-e2e"));

        System.out.println("ETAPA 4: Acesso protegido OK");
    }

    @Test
    @DisplayName("Não deve acessar endpoint protegido sem token")
    void shouldNotAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/laboratories"))
                .andExpect(status().isForbidden());

        System.out.println("ETAPA: Sem token → 403 OK");
    }

    @Test
    @DisplayName("Não deve acessar endpoint protegido com token inválido")
    void shouldNotAccessProtectedEndpointWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/laboratories")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isForbidden());

        System.out.println("ETAPA: Token inválido → 403 OK");
    }

    @Test
    @DisplayName("Não deve registrar com email duplicado")
    void shouldNotRegisterWithDuplicateEmail() throws Exception {
        // Primeiro registro
        RegisterRequest request = new RegisterRequest();
        request.setName("João Silva");
        request.setEmail("duplicado@labcloud.com");
        request.setPassword("senha123");
        request.setLaboratoryName("Lab A");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Tentar registrar de novo com mesmo email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Resource Already Exists"));

        System.out.println("ETAPA: Email duplicado → 409 OK");
    }
}
