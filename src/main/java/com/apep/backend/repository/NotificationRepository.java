package com.apep.backend.repository;

import com.apep.backend.domain.Notification;
import com.apep.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification> {
    List<Notification> findByUser(User user);
    Page<Notification> findByUser(User user, Pageable pageable);
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByUserAndType(User user, Notification.NotificationType type);
    long countByUserAndIsReadFalse(User user);
} 