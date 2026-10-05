package com.labcloud.sample.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.labcloud.sample.dto.external.LaboratoryClientResponse;
import com.labcloud.sample.dto.external.UserClientResponse;

/*
    O client Feign vai realizar a comunicação
    com o auth-service. O Feign converte 
    automaticamente essas chamadas em HTTP REST.
*/

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8081}")
public interface AuthClient {

    // Laboratório
    @GetMapping("/api/laboratories/{id}")
    LaboratoryClientResponse getLaboratory(@PathVariable String id);

    // Usuário
    @GetMapping("/api/users/{id}")
    UserClientResponse getUser(@PathVariable String id);

}
