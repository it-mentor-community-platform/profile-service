package service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.exception.ProfileAlreadyExistException;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.service.ProfilePersistenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePersistenceServiceTest {

    private static final Long TEST_TELEGRAM_USER_ID = 111L;
    private static final String TEST_GITHUB_URL = "https://github.com/ivan";
    private static final String TEST_FIRST_NAME = "Ivan";

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfilePersistenceService profilePersistenceService;

    @Test
    @DisplayName("Создание профиля")
    void shouldCreateProfile() {
        Map<String, String> details = Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), TEST_FIRST_NAME);
        when(profileRepository.findByTelegramUserId(TEST_TELEGRAM_USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Profile result = profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, details);
        assertEquals(TEST_TELEGRAM_USER_ID, result.getTelegramUserId());
        assertEquals(Set.of(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), TEST_FIRST_NAME)),
                result.getDetails());
    }

    @Test
    @DisplayName("Создание профиля: Telegram ID уже занят")
    void shouldThrowException_WhenTelegramIdAlreadyExists() {
        Profile existingProfile = new Profile(1L, TEST_TELEGRAM_USER_ID, Set.of());
        when(profileRepository.findByTelegramUserId(TEST_TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));
        assertThrows(ProfileAlreadyExistException.class,
                () -> profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID,
                        Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), TEST_FIRST_NAME)));
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("Создание профиля: занятый GitHub URL пропускается")
    void shouldSkipGithubUrl_WhenItAlreadyExists() {
        Map<String, String> details = Map.of(
                ProfileDetailType.FIRST_NAME.getDetailName(), TEST_FIRST_NAME,
                ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), TEST_GITHUB_URL
        );
        Profile anotherProfile = new Profile(2L, 222L, Set.of());
        when(profileRepository.findByTelegramUserId(TEST_TELEGRAM_USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findProfileByGitHubUrl(TEST_GITHUB_URL)).thenReturn(Optional.of(anotherProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Profile result = profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, details);
        assertEquals(Set.of(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), TEST_FIRST_NAME)),
                result.getDetails());
    }

    @Test
    @DisplayName("Обновление профиля: новые детали добавляются, старые сохраняются")
    void shouldUpdateProfile_WithoutDeletingExistingDetails() {
        Set<ProfileDetail> existingDetails = Set.of(
                new ProfileDetail(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), TEST_GITHUB_URL),
                new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "OldName")
        );
        Profile existingProfile = new Profile(1L, TEST_TELEGRAM_USER_ID, new HashSet<>(existingDetails));
        when(profileRepository.findByTelegramUserId(TEST_TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Profile result = profilePersistenceService.updateProfile(TEST_TELEGRAM_USER_ID,
                Map.of(ProfileDetailType.LAST_NAME.getDetailName(), "Ivanov"));
        assertEquals(Set.of(
                        new ProfileDetail(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), TEST_GITHUB_URL),
                        new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "OldName"),
                        new ProfileDetail(ProfileDetailType.LAST_NAME.getDetailName(), "Ivanov")
                ), result.getDetails());
    }

    @Test
    @DisplayName("Обновление профиля: существующая деталь заменяется новой")
    void shouldReplaceExistingDetail_WhenUpdatingProfile() {
        Profile existingProfile = new Profile(1L, TEST_TELEGRAM_USER_ID,
                new HashSet<>(Set.of(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "OldName"))));
        when(profileRepository.findByTelegramUserId(TEST_TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Profile result = profilePersistenceService.updateProfile(TEST_TELEGRAM_USER_ID,
                Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), "NewName"));
        assertEquals(Set.of(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "NewName")),
                result.getDetails());
    }
}