package com.example.demo;

import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController

public class ObservabilityController {

    @Autowired
    private HttpBinService httpBinService;

    @Observed(name = "observability.controller.get.data")
    @GetMapping("/data")
    public ResponseEntity<String> retrieveSampleData() throws Exception {
        String sampleData = httpBinService.hello();
        return ResponseEntity.ok(sampleData);
    }
}