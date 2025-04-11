package com.apep.backend.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthIndicator implements HealthIndicator {
    
    private final RedisConnectionFactory redisConnectionFactory;
    
    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }
    
    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.ping();
            return Health.up()
                    .withDetail("database", "Redis")
                    .withDetail("status", "UP")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Redis")
                    .withDetail("status", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
} 