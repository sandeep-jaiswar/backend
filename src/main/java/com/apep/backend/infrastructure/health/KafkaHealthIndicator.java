package com.apep.backend.infrastructure.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    private final KafkaAdmin kafkaAdmin;
    
    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }
    
    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterResult describeCluster = adminClient.describeCluster();
            
            String clusterId = describeCluster.clusterId().get(5, TimeUnit.SECONDS);
            int nodeCount = describeCluster.nodes().get(5, TimeUnit.SECONDS).size();
            
            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodeCount", nodeCount)
                    .withDetail("status", "UP")
                    .build();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }
} 