package com.apep.backend.repository;

import com.apep.backend.domain.Chat;
import com.apep.backend.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends BaseRepository<Chat> {
    Optional<Chat> findByUser1AndUser2(User user1, User user2);
    List<Chat> findByUser1OrUser2(User user1, User user2);
    List<Chat> findByUser1AndIsActiveTrue(User user1);
    List<Chat> findByUser2AndIsActiveTrue(User user2);
} 