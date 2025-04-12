package com.apep.backend.service;

import com.apep.backend.domain.User;
import com.apep.backend.domain.UserProfileView;
import com.apep.backend.repository.UserProfileViewRepository;
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
public class UserProfileViewServiceTest {

    @Mock
    private UserProfileViewRepository userProfileViewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileViewService userProfileViewService;

    private User viewer;
    private User viewed;
    private UserProfileView profileView;

    @BeforeEach
    void setUp() {
        viewer = new User();
        viewer.setId(1L);
        viewer.setUsername("viewer");
        viewer.setEmail("viewer@example.com");

        viewed = new User();
        viewed.setId(2L);
        viewed.setUsername("viewed");
        viewed.setEmail("viewed@example.com");

        profileView = new UserProfileView();
        profileView.setId(1L);
        profileView.setViewer(viewer);
        profileView.setViewed(viewed);
        profileView.setViewedAt(LocalDateTime.now());
    }

    @Test
    void recordProfileViewSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(viewed));
        when(userProfileViewRepository.save(any(UserProfileView.class))).thenReturn(profileView);

        UserProfileView result = userProfileViewService.recordProfileView(1L, 2L);

        assertNotNull(result);
        assertEquals(viewer, result.getViewer());
        assertEquals(viewed, result.getViewed());
        assertNotNull(result.getViewedAt());
        verify(userProfileViewRepository).save(any(UserProfileView.class));
    }

    @Test
    void recordProfileViewViewerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.recordProfileView(1L, 2L);
        });

        verify(userProfileViewRepository, never()).save(any(UserProfileView.class));
    }

    @Test
    void recordProfileViewViewedNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.recordProfileView(1L, 2L);
        });

        verify(userProfileViewRepository, never()).save(any(UserProfileView.class));
    }

    @Test
    void getProfileViewsForUserSuccessful() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(viewed));
        when(userProfileViewRepository.findByViewedIdOrderByViewedAtDesc(2L))
                .thenReturn(Arrays.asList(profileView));

        List<UserProfileView> views = userProfileViewService.getProfileViewsForUser(2L);

        assertNotNull(views);
        assertFalse(views.isEmpty());
        assertEquals(1, views.size());
        assertEquals(viewer.getId(), views.get(0).getViewer().getId());
        assertEquals(viewed.getId(), views.get(0).getViewed().getId());
    }

    @Test
    void getProfileViewsForUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.getProfileViewsForUser(2L);
        });
    }

    @Test
    void getRecentViewersByUserSuccessful() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(viewed));
        when(userProfileViewRepository.findDistinctViewersByViewedIdOrderByMaxViewedAtDesc(2L))
                .thenReturn(Arrays.asList(viewer));

        List<User> viewers = userProfileViewService.getRecentViewersByUser(2L);

        assertNotNull(viewers);
        assertFalse(viewers.isEmpty());
        assertEquals(1, viewers.size());
        assertEquals(viewer.getId(), viewers.get(0).getId());
    }

    @Test
    void getRecentViewersByUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.getRecentViewersByUser(2L);
        });
    }

    @Test
    void deleteProfileViewSuccessful() {
        when(userProfileViewRepository.findById(1L)).thenReturn(Optional.of(profileView));
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));

        userProfileViewService.deleteProfileView(1L, 1L);

        verify(userProfileViewRepository).delete(profileView);
    }

    @Test
    void deleteProfileViewNotFound() {
        when(userProfileViewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.deleteProfileView(1L, 1L);
        });

        verify(userProfileViewRepository, never()).delete(any(UserProfileView.class));
    }

    @Test
    void deleteProfileViewUnauthorized() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);

        when(userProfileViewRepository.findById(1L)).thenReturn(Optional.of(profileView));
        when(userRepository.findById(3L)).thenReturn(Optional.of(unauthorizedUser));

        assertThrows(RuntimeException.class, () -> {
            userProfileViewService.deleteProfileView(3L, 1L);
        });

        verify(userProfileViewRepository, never()).delete(any(UserProfileView.class));
    }
}