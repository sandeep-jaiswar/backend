package com.apep.backend.infrastructure;

import com.apep.backend.domain.User;
import com.apep.backend.dto.GoogleUserInfo;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService extends DefaultOAuth2UserService {

    private final UserService userService;
    private static final String OAUTH2_ERROR_CODE = "oauth2_error";
    private static final String EMAIL_REQUIRED_ERROR = "Email is required";
    private static final String OAUTH2_LOGIN_ERROR = "Failed to process Google OAuth login";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            if (!StringUtils.hasText(email)) {
                log.error("Email is missing from Google OAuth response");
                throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, EMAIL_REQUIRED_ERROR, null));
            }

            // Upsert user in our database
            User user = userService.upsertUserFromGoogle(email, name, picture);
            log.info("Successfully processed Google OAuth login for user: {}", email);

            // Create GoogleUserInfo with our user's ID
            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.setId(user.getId().toString());
            userInfo.setEmail(email);
            userInfo.setName(name);
            userInfo.setPicture(picture);
            userInfo.setLocale(oAuth2User.getAttribute("locale"));

            return oAuth2User;
        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth2 authentication error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error processing Google OAuth login", e);
            throw new OAuth2AuthenticationException(new OAuth2Error(OAUTH2_ERROR_CODE, OAUTH2_LOGIN_ERROR, null), e);
        }
    }
}