package com.apep.backend.service;

import com.apep.backend.domain.Friendship;
import com.apep.backend.domain.User;
import com.apep.backend.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public Friendship sendFriendRequest(User user, User friend) {
        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public Friendship acceptFriendRequest(Friendship friendship) {
        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public void rejectFriendRequest(Friendship friendship) {
        friendship.setStatus(Friendship.FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getUserFriendships(User user) {
        return friendshipRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getPendingFriendRequests(User user) {
        return friendshipRepository.findByFriendAndStatus(user, Friendship.FriendshipStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public long getFriendCount(User user) {
        return friendshipRepository.countByUserAndStatus(user, Friendship.FriendshipStatus.ACCEPTED);
    }
}