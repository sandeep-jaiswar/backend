package com.apep.backend.repository;

import com.apep.backend.domain.Friendship;
import com.apep.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    List<Friendship> findByUser(User user);
    List<Friendship> findByFriend(User friend);
    List<Friendship> findByUserAndStatus(User user, Friendship.FriendshipStatus status);
    List<Friendship> findByFriendAndStatus(User friend, Friendship.FriendshipStatus status);
    
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.user = :user AND f.status = :status")
    long countByUserAndStatus(User user, Friendship.FriendshipStatus status);
} 