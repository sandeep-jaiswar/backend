package com.apep.backend.api;

import com.apep.backend.dto.ApiResponse;
import com.apep.backend.dto.GoogleUserInfo;
import com.apep.backend.infrastructure.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<String>> login() {
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Redirecting to Google login...", null));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<GoogleUserInfo>> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            log.warn("Unauthorized access attempt to /user endpoint");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("error", "User not authenticated", null));
        }

        try {
            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.setId(principal.getAttribute("sub"));
            userInfo.setEmail(principal.getAttribute("email"));
            userInfo.setName(principal.getAttribute("name"));
            userInfo.setPicture(principal.getAttribute("picture"));
            userInfo.setLocale(principal.getAttribute("locale"));

            return ResponseEntity.ok(
                    new ApiResponse<>("success", "User information retrieved successfully", userInfo));
        } catch (Exception e) {
            log.error("Error retrieving user information", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", "Failed to retrieve user information", null));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Auth controller error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("error", "An unexpected error occurred", null));
    }
}