package com.apep.backend.api;

import com.apep.backend.dto.GoogleUserInfo;
import com.apep.backend.infrastructure.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleAuthService googleAuthService;

    public AuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        // This endpoint will be automatically handled by Spring Security OAuth2
        return ResponseEntity.ok("Redirecting to Google login...");
    }

    @GetMapping("/user")
    public ResponseEntity<GoogleUserInfo> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }

        GoogleUserInfo userInfo = new GoogleUserInfo();
        userInfo.setId(principal.getAttribute("sub"));
        userInfo.setEmail(principal.getAttribute("email"));
        userInfo.setName(principal.getAttribute("name"));
        userInfo.setPicture(principal.getAttribute("picture"));
        userInfo.setLocale(principal.getAttribute("locale"));

        return ResponseEntity.ok(userInfo);
    }
}