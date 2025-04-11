package com.apep.backend.service;

import com.apep.backend.domain.Chat;
import com.apep.backend.domain.Message;
import com.apep.backend.domain.User;
import com.apep.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private static final int MAX_MESSAGE_LENGTH = 1000;
    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Transactional
    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public Message sendMessage(Chat chat, User sender, String content) {
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        if (content.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message content exceeds maximum length");
        }

        try {
            Message message = new Message();
            message.setChat(chat);
            message.setSender(sender);
            message.setContent(content);
            return messageRepository.save(message);
        } catch (Exception e) {
            log.error("Error sending message in chat {} by user {}", chat.getId(), sender.getId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Message> getChatMessages(Chat chat, Pageable pageable) {
        try {
            return messageRepository.findByChat(chat, pageable);
        } catch (Exception e) {
            log.error("Error retrieving messages for chat {}", chat.getId(), e);
            throw new RuntimeException("Failed to retrieve messages", e);
        }
    }

    @Transactional
    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public void markMessagesAsRead(Chat chat, User user) {
        try {
            List<Message> unreadMessages = messageRepository.findByChatAndIsReadFalse(chat);

            // Process messages in batches
            for (int i = 0; i < unreadMessages.size(); i += BATCH_SIZE) {
                List<Message> batch = unreadMessages.subList(i, Math.min(i + BATCH_SIZE, unreadMessages.size()));
                batch.forEach(message -> {
                    if (!message.getSender().equals(user)) {
                        message.setRead(true);
                    }
                });
                messageRepository.saveAll(batch);
            }
        } catch (Exception e) {
            log.error("Error marking messages as read in chat {} for user {}", chat.getId(), user.getId(), e);
            throw new RuntimeException("Failed to mark messages as read", e);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Chat chat) {
        try {
            return messageRepository.countByChatAndIsReadFalse(chat);
        } catch (Exception e) {
            log.error("Error counting unread messages for chat {}", chat.getId(), e);
            throw new RuntimeException("Failed to count unread messages", e);
        }
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Long> getUnreadMessageCountAsync(Chat chat) {
        return CompletableFuture.supplyAsync(() -> getUnreadMessageCount(chat))
                .exceptionally(throwable -> {
                    log.error("Error in async unread message count for chat {}", chat.getId(), throwable);
                    return 0L;
                });
    }

    @Transactional
    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public void deleteOldMessages(LocalDateTime cutoffDate) {
        try {
            messageRepository.deleteByTimestampBefore(cutoffDate);
        } catch (Exception e) {
            log.error("Error deleting old messages before {}", cutoffDate, e);
            throw new RuntimeException("Failed to delete old messages", e);
        }
    }

    @Transactional
    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public void softDeleteMessage(Message message) {
        try {
            message.setDeleted(true);
            messageRepository.save(message);
        } catch (Exception e) {
            log.error("Error soft deleting message {}", message.getId(), e);
            throw new RuntimeException("Failed to delete message", e);
        }
    }
}