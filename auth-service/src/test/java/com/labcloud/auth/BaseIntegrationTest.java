package com.labcloud.auth;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

@SpringBootTest 
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional 
public class BaseIntegrationTest {

}
