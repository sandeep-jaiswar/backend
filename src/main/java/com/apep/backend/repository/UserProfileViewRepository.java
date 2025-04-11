package com.apep.backend.repository;

import com.apep.backend.domain.UserProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileViewRepository extends JpaRepository<UserProfileView, Long> {
    Optional<UserProfileView> findById(Long id);

    @Query("SELECT u FROM UserProfileView u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
    List<UserProfileView> searchUsers(String query);

    @Query("SELECT u FROM UserProfileView u ORDER BY u.lastActive DESC")
    List<UserProfileView> findRecentlyActiveUsers();
}