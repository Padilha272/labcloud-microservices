package com.labcloud.result;

import com.labcloud.result.clients.AuthClient;
import com.labcloud.result.clients.SampleClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base para testes de integração do Result Service.
 *
 * Mocka os 2 Feign Clients para não depender de Auth/Sample rodando!
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @MockBean
    protected AuthClient authClient;

    @MockBean
    protected SampleClient sampleClient;
}