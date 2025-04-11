package com.apep.backend.infrastructure;

import com.apep.backend.dto.GoogleUserInfo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        GoogleUserInfo userInfo = new GoogleUserInfo();
        userInfo.setId(oAuth2User.getAttribute("sub"));
        userInfo.setEmail(oAuth2User.getAttribute("email"));
        userInfo.setName(oAuth2User.getAttribute("name"));
        userInfo.setPicture(oAuth2User.getAttribute("picture"));
        userInfo.setLocale(oAuth2User.getAttribute("locale"));

        return oAuth2User;
    }
}