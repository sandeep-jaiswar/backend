package com.apep.backend.service;

import com.apep.backend.domain.Friendship;
import com.apep.backend.domain.User;
import com.apep.backend.repository.FriendshipRepository;
import com.apep.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private User user1;
    private User user2;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        friendship = new Friendship();
        friendship.setId(1L);
        friendship.setUser1(user1);
        friendship.setUser2(user2);
        friendship.setStatus("PENDING");
    }

    @Test
    void sendFriendRequestSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.findBetweenUsers(1L, 2L)).thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship result = friendshipService.sendFriendRequest(1L, 2L);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(user1, result.getUser1());
        assertEquals(user2, result.getUser2());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequestUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            friendshipService.sendFriendRequest(1L, 2L);
        });

        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequestAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.findBetweenUsers(1L, 2L)).thenReturn(Optional.of(friendship));

        assertThrows(RuntimeException.class, () -> {
            friendshipService.sendFriendRequest(1L, 2L);
        });

        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void acceptFriendRequestSuccessful() {
        friendship.setStatus("PENDING");
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship result = friendshipService.acceptFriendRequest(2L, 1L);

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void acceptFriendRequestNotFound() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            friendshipService.acceptFriendRequest(2L, 1L);
        });

        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void acceptFriendRequestUnauthorized() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> {
            friendshipService.acceptFriendRequest(3L, 1L);
        });

        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void getFriendsSuccessful() {
        friendship.setStatus("ACCEPTED");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(friendshipRepository.findAllByUserIdAndStatus(1L, "ACCEPTED"))
                .thenReturn(Arrays.asList(friendship));

        List<User> friends = friendshipService.getFriends(1L);

        assertNotNull(friends);
        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }

    @Test
    void getFriendsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            friendshipService.getFriends(1L);
        });
    }

    @Test
    void deleteFriendshipSuccessful() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        friendshipService.deleteFriendship(1L, 1L);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void deleteFriendshipNotFound() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            friendshipService.deleteFriendship(1L, 1L);
        });

        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }
}