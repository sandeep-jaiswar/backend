package com.apep.backend.service;

import com.apep.backend.domain.User;
import com.apep.backend.dto.LoginRequest;
import com.apep.backend.dto.RegisterRequest;
import com.apep.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setUsername("testuser");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setUsername("testuser");
        existingUser.setPassword("hashedPassword123");
    }

    @Test
    void registerSuccessful() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        String token = userService.register(validRegisterRequest);

        assertNotNull(token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerFailureEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSuccessful() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String token = userService.login(validLoginRequest);

        assertNotNull(token);
    }

    @Test
    void loginFailureInvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userService.login(validLoginRequest);
        });
    }

    @Test
    void loginFailureUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.login(validLoginRequest);
        });
    }

    @Test
    void getUserByIdSuccessful() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));

        User user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals(existingUser.getId(), user.getId());
        assertEquals(existingUser.getEmail(), user.getEmail());
    }

    @Test
    void getUserByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(1L);
        });
    }
}