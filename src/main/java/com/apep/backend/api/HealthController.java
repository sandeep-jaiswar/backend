package com.apep.backend.api;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final Map<String, HealthIndicator> healthIndicators;

    public HealthController(Map<String, HealthIndicator> healthIndicators) {
        this.healthIndicators = healthIndicators;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = healthIndicators.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().replace("HealthIndicator", "").toLowerCase(),
                        entry -> {
                            Health health = entry.getValue().health();
                            return Map.of(
                                    "status", health.getStatus().getCode(),
                                    "details", health.getDetails()
                            );
                        }
                ));

        boolean allUp = healthStatus.values().stream()
                .allMatch(status -> "UP".equals(((Map<?, ?>) status).get("status")));

        return ResponseEntity.status(allUp ? 200 : 503)
                .body(Map.of(
                        "status", allUp ? "UP" : "DOWN",
                        "components", healthStatus
                ));
    }

    @GetMapping("/postgres")
    public ResponseEntity<Map<String, Object>> postgresHealth() {
        return getComponentHealth("postgresHealthIndicator");
    }

    @GetMapping("/redis")
    public ResponseEntity<Map<String, Object>> redisHealth() {
        return getComponentHealth("redisHealthIndicator");
    }

    @GetMapping("/kafka")
    public ResponseEntity<Map<String, Object>> kafkaHealth() {
        return getComponentHealth("kafkaHealthIndicator");
    }

    private ResponseEntity<Map<String, Object>> getComponentHealth(String indicatorName) {
        HealthIndicator indicator = healthIndicators.get(indicatorName);
        if (indicator == null) {
            return ResponseEntity.notFound().build();
        }

        Health health = indicator.health();
        return ResponseEntity.status(health.getStatus().equals(Health.up().build().getStatus()) ? 200 : 503)
                .body(Map.of(
                        "status", health.getStatus().getCode(),
                        "details", health.getDetails()
                ));
    }
} 