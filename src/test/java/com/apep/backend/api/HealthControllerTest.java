package com.apep.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheckShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void detailedHealthCheckShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/health/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.version").exists())
                .andExpect(jsonPath("$.details.memory").exists())
                .andExpect(jsonPath("$.details.processors").exists());
    }

    @Test
    void metricsEndpointShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/health/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.metrics.memory").exists())
                .andExpect(jsonPath("$.metrics.threads").exists())
                .andExpect(jsonPath("$.metrics.classes").exists());
    }

    @Test
    void environmentEndpointShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/health/env"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.environment").exists())
                .andExpect(jsonPath("$.environment.java").exists())
                .andExpect(jsonPath("$.environment.os").exists());
    }
}