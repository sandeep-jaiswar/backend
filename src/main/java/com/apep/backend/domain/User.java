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

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the initiatedChats
	 */
	public Set<Chat> getInitiatedChats() {
		return initiatedChats;
	}

	/**
	 * @param initiatedChats the initiatedChats to set
	 */
	public void setInitiatedChats(Set<Chat> initiatedChats) {
		this.initiatedChats = initiatedChats;
	}

	/**
	 * @return the receivedChats
	 */
	public Set<Chat> getReceivedChats() {
		return receivedChats;
	}

	/**
	 * @param receivedChats the receivedChats to set
	 */
	public void setReceivedChats(Set<Chat> receivedChats) {
		this.receivedChats = receivedChats;
	}

	/**
	 * @return the sentMessages
	 */
	public Set<Message> getSentMessages() {
		return sentMessages;
	}

	/**
	 * @param sentMessages the sentMessages to set
	 */
	public void setSentMessages(Set<Message> sentMessages) {
		this.sentMessages = sentMessages;
	}

	/**
	 * @return the friendships
	 */
	public Set<Friendship> getFriendships() {
		return friendships;
	}

	/**
	 * @param friendships the friendships to set
	 */
	public void setFriendships(Set<Friendship> friendships) {
		this.friendships = friendships;
	}

	/**
	 * @return the friendOf
	 */
	public Set<Friendship> getFriendOf() {
		return friendOf;
	}

	/**
	 * @param friendOf the friendOf to set
	 */
	public void setFriendOf(Set<Friendship> friendOf) {
		this.friendOf = friendOf;
	}

	/**
	 * @return the randomChatQueues
	 */
	public Set<RandomChatQueue> getRandomChatQueues() {
		return randomChatQueues;
	}

	/**
	 * @param randomChatQueues the randomChatQueues to set
	 */
	public void setRandomChatQueues(Set<RandomChatQueue> randomChatQueues) {
		this.randomChatQueues = randomChatQueues;
	}

	/**
	 * @return the notifications
	 */
	public Set<Notification> getNotifications() {
		return notifications;
	}

	/**
	 * @param notifications the notifications to set
	 */
	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}
    
    
}