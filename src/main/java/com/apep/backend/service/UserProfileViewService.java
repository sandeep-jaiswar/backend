package com.apep.backend.service;

import com.apep.backend.domain.User;
import com.apep.backend.domain.UserProfileView;
import com.apep.backend.repository.UserProfileViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileViewService {
    private UserProfileViewRepository userProfileViewRepository;
    private UserService userService;
    private MessageService messageService;
    private FriendshipService friendshipService;

    @Cacheable(value = "userProfile", key = "#userId")
    @Transactional(readOnly = true)
    public Optional<UserProfileView> getUserProfileView(Long userId) {
        return userProfileViewRepository.findById(userId);
    }

    @Cacheable(value = "userSearch", key = "#query")
    @Transactional(readOnly = true)
    public List<UserProfileView> searchUsers(String query) {
        return userProfileViewRepository.searchUsers(query);
    }

    @Cacheable(value = "activeUsers")
    @Transactional(readOnly = true)
    public List<UserProfileView> getRecentlyActiveUsers() {
        return userProfileViewRepository.findRecentlyActiveUsers();
    }

    @Scheduled(fixedRate = 300000) // Update every 5 minutes
    @Transactional
    @CacheEvict(value = { "userProfile", "userSearch", "activeUsers" }, allEntries = true)
    public void refreshMaterializedView() {
        // This would typically be a native SQL query to refresh the materialized view
        // For example:
        // entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY
        // user_profile_view").executeUpdate();
    }

    @CacheEvict(value = "userProfile", key = "#userId")
    public void updateUserLastActive(Long userId) {
        // This would update the last_active timestamp in the materialized view
        // Could be called from a WebSocket handler or other service when user activity
        // is detected
    }

    @CacheEvict(value = { "userProfile", "userSearch", "activeUsers" }, allEntries = true)
    public void invalidateAllCaches() {
        // Method to manually invalidate all caches if needed
    }
}