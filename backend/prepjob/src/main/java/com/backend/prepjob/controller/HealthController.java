package com.backend.prepjob.controller;

import com.backend.prepjob.model.HealthCheck;
import com.backend.prepjob.repo.HealthCheckRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthCheckRepo  healthCheckRepo;

    public HealthController(HealthCheckRepo healthCheckRepo) {
        this.healthCheckRepo = healthCheckRepo;
    }

    @GetMapping
    public String health() {

        HealthCheck healthCheck = healthCheckRepo.findFirstBy()
                        .orElse(new HealthCheck());
        healthCheck.setCreatedAt(LocalDateTime.now());
        healthCheckRepo.save(healthCheck);
        return "OK";
    }
}
