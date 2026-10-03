package com.labcloud.sample;

import com.labcloud.sample.clients.AuthClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base para todos os testes de integração do Sample Service.
 *
 * Mocka o AuthClient para não depender do Auth Service real!
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    // AuthClient é mockado globalmente para todos os testes que estendem esta
    // O MockBean faz com que todos os testes que estendem essa classe usem um AuthClient mockado
    // classe
    @MockBean
    protected AuthClient authClient;
}