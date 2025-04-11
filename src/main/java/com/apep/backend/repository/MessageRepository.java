package com.apep.backend.repository;

import com.apep.backend.domain.Chat;
import com.apep.backend.domain.Message;
import com.apep.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChat(Chat chat);
    Page<Message> findByChat(Chat chat, Pageable pageable);
    List<Message> findByChatAndSender(Chat chat, User sender);
    List<Message> findByChatAndIsReadFalse(Chat chat);
    long countByChatAndIsReadFalse(Chat chat);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.timestamp < :cutoffDate")
    void deleteByTimestampBefore(LocalDateTime cutoffDate);
} 