package com.apep.backend;

import com.apep.backend.domain.User;
import com.apep.backend.dto.LoginRequest;
import com.apep.backend.dto.RegisterRequest;
import com.apep.backend.repository.UserRepository;
import com.apep.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BackendApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		assertNotNull(userService);
		assertNotNull(userRepository);
	}

	@Test
	void integrationTestUserRegistrationAndLogin() {
		// Clean up any existing test user
		userRepository.findByEmail("test@example.com")
				.ifPresent(user -> userRepository.delete(user));

		// Register a new user
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setEmail("test@example.com");
		registerRequest.setPassword("password123");
		registerRequest.setUsername("testuser");

		ResponseEntity<String> registerResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/auth/register",
				registerRequest,
				String.class);

		assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
		assertNotNull(registerResponse.getBody());

		// Login with the registered user
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password123");

		ResponseEntity<String> loginResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/auth/login",
				loginRequest,
				String.class);

		assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		assertNotNull(loginResponse.getBody());
	}

	@Test
	void integrationTestHealthEndpoints() {
		// Test basic health endpoint
		ResponseEntity<String> healthResponse = restTemplate.getForEntity(
				"http://localhost:" + port + "/api/health",
				String.class);
		assertEquals(HttpStatus.OK, healthResponse.getStatusCode());

		// Test detailed health endpoint
		ResponseEntity<String> detailedHealthResponse = restTemplate.getForEntity(
				"http://localhost:" + port + "/api/health/details",
				String.class);
		assertEquals(HttpStatus.OK, detailedHealthResponse.getStatusCode());

		// Test metrics endpoint
		ResponseEntity<String> metricsResponse = restTemplate.getForEntity(
				"http://localhost:" + port + "/api/health/metrics",
				String.class);
		assertEquals(HttpStatus.OK, metricsResponse.getStatusCode());
	}

	@Test
	void integrationTestUserProfileFlow() {
		// Register a test user
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setEmail("profile@example.com");
		registerRequest.setPassword("password123");
		registerRequest.setUsername("profileuser");

		ResponseEntity<String> registerResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/auth/register",
				registerRequest,
				String.class);

		assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
		String token = registerResponse.getBody();
		assertNotNull(token);

		// Get user profile
		User user = userRepository.findByEmail("profile@example.com").orElse(null);
		assertNotNull(user);

		ResponseEntity<String> profileResponse = restTemplate.getForEntity(
				"http://localhost:" + port + "/api/users/" + user.getId(),
				String.class);

		assertEquals(HttpStatus.OK, profileResponse.getStatusCode());
		assertNotNull(profileResponse.getBody());
	}
}
