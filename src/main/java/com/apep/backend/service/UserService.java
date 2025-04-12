package com.apep.backend.service;

import com.apep.backend.domain.Profile;
import com.apep.backend.domain.User;
import com.apep.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User upsertUserFromGoogle(String email, String name, String picture) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Update existing user
                    existingUser.setUsername(name);
                    if (existingUser.getProfile() == null) {
                        Profile profile = new Profile();
                        profile.setUser(existingUser);
                        profile.setProfilePicture(picture);
                        existingUser.setProfile(profile);
                    } else {
                        existingUser.getProfile().setProfilePicture(picture);
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Create new user
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password for
                                                                                               // OAuth users

                    Profile profile = new Profile();
                    profile.setUser(newUser);
                    profile.setProfilePicture(picture);
                    newUser.setProfile(profile);

                    return userRepository.save(newUser);
                });
    }
}