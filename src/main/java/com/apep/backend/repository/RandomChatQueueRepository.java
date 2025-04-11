package com.apep.backend.repository;

import com.apep.backend.domain.RandomChatQueue;
import com.apep.backend.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RandomChatQueueRepository extends BaseRepository<RandomChatQueue> {
    Optional<RandomChatQueue> findByUser(User user);
    List<RandomChatQueue> findByIsActiveTrue();
    boolean existsByUserAndIsActiveTrue(User user);
} 