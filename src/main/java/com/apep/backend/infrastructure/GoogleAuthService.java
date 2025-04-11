package com.apep.backend.infrastructure;

import com.apep.backend.domain.User;
import com.apep.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService extends DefaultOAuth2UserService {

    private final UserService userService;
    private static final String OAUTH2_ERROR_CODE = "oauth2_error";
    private static final String EMAIL_REQUIRED_ERROR = "Email is required";
    private static final String OAUTH2_LOGIN_ERROR = "Failed to process Google OAuth login";
    private static final String EMAIL_NOT_VERIFIED_ERROR = "Email is not verified";
    private static final String INVALID_PROVIDER_ERROR = "Invalid OAuth2 provider";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            // Validate OAuth2 provider
            if (!"google".equals(userRequest.getClientRegistration().getRegistrationId())) {
                log.error("Invalid OAuth2 provider: {}", userRequest.getClientRegistration().getRegistrationId());
                throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, INVALID_PROVIDER_ERROR, null));
            }

            OAuth2User oAuth2User = super.loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();

            // Extract and validate required attributes
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            Boolean emailVerified = (Boolean) attributes.get("email_verified");

            if (!StringUtils.hasText(email)) {
                log.error("Email is missing from Google OAuth response");
                throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, EMAIL_REQUIRED_ERROR, null));
            }

            if (emailVerified == null || !emailVerified) {
                log.error("Email is not verified for user: {}", email);
                throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, EMAIL_NOT_VERIFIED_ERROR, null));
            }

            // Upsert user in our database
            User user = userService.upsertUserFromGoogle(email, name, picture);
            if (user == null) {
                log.error("Failed to create/update user for email: {}", email);
                throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, OAUTH2_LOGIN_ERROR, null));
            }

            log.info("Successfully processed Google OAuth login for user: {}", email);

            // Return CustomOAuth2User with our user's ID and additional attributes
            return new CustomOAuth2User(user, attributes);
        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth2 authentication error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error processing Google OAuth login", e);
            throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, OAUTH2_LOGIN_ERROR, null), e);
        }
    }
}