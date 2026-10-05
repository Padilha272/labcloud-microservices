package com.labcloud.result.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.labcloud.result.dto.external.UserClientResponse;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthClient {

    @GetMapping("/api/users/{id}")
    UserClientResponse getUser(@PathVariable String id);
}
