package com.apep.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user1")
    private Set<Chat> initiatedChats = new HashSet<>();

    @OneToMany(mappedBy = "user2")
    private Set<Chat> receivedChats = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<Message> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Friendship> friendships = new HashSet<>();

    @OneToMany(mappedBy = "friend")
    private Set<Friendship> friendOf = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<RandomChatQueue> randomChatQueues = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications = new HashSet<>();
}