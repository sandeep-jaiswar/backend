package com.apep.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile_view")
@Immutable
@Getter
@Setter
public class UserProfileView {
    @Id
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "bio")
    private String bio;

    @Column(name = "location")
    private String location;

    @Column(name = "friend_count")
    private Long friendCount;

    @Column(name = "unread_messages")
    private Long unreadMessages;

    @Column(name = "last_active")
    private LocalDateTime lastActive;
}