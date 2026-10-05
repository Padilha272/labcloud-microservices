package com.labcloud.result.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.labcloud.result.dto.external.SampleClientResponse;

@FeignClient(name = "sample-service", url = "${sample.service.url}")
public interface SampleClient {
    @GetMapping("/api/samples/{id}")
    SampleClientResponse getSample(@PathVariable String id);
}
