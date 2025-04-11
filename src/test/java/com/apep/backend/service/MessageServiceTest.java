package com.apep.backend.service;

import com.apep.backend.domain.Message;
import com.apep.backend.domain.User;
import com.apep.backend.dto.MessageRequest;
import com.apep.backend.repository.MessageRepository;
import com.apep.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User recipient;
    private Message message;
    private MessageRequest messageRequest;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setUsername("sender");

        recipient = new User();
        recipient.setId(2L);
        recipient.setEmail("recipient@example.com");
        recipient.setUsername("recipient");

        message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent("Test message");
        message.setTimestamp(LocalDateTime.now());

        messageRequest = new MessageRequest();
        messageRequest.setRecipientId(2L);
        messageRequest.setContent("Test message");
    }

    @Test
    void sendMessageSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        Message result = messageService.sendMessage(1L, messageRequest);

        assertNotNull(result);
        assertEquals(message.getContent(), result.getContent());
        assertEquals(message.getSender(), result.getSender());
        assertEquals(message.getRecipient(), result.getRecipient());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessageSenderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(1L, messageRequest);
        });

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessageRecipientNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(1L, messageRequest);
        });

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void getMessagesBetweenUsersSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        when(messageRepository.findMessagesBetweenUsers(sender.getId(), recipient.getId()))
                .thenReturn(Arrays.asList(message));

        List<Message> messages = messageService.getMessagesBetweenUsers(1L, 2L);

        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertEquals(1, messages.size());
        assertEquals(message.getContent(), messages.get(0).getContent());
    }

    @Test
    void getMessagesBetweenUsersUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.getMessagesBetweenUsers(1L, 2L);
        });
    }

    @Test
    void deleteMessageSuccessful() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        messageService.deleteMessage(1L, 1L);

        verify(messageRepository).delete(message);
    }

    @Test
    void deleteMessageNotFound() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.deleteMessage(1L, 1L);
        });

        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteMessageUnauthorized() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(userRepository.findById(3L)).thenReturn(Optional.of(unauthorizedUser));

        assertThrows(RuntimeException.class, () -> {
            messageService.deleteMessage(3L, 1L);
        });

        verify(messageRepository, never()).delete(any(Message.class));
    }
}