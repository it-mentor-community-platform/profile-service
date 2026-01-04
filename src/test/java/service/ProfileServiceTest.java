package service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.event.UserCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    private final Long TELEGRAM_USER_ID = 111L;

    @Test
    @DisplayName("Создание профиля: только имя")
    void shouldCreateProfile_WithOnlyFirstName() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("Ivan")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.empty());

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertEquals(1, savedProfile.getDetails().size());
        assertTrue(hasDetail(savedProfile, ProfileDetailType.FIRST_NAME, "Ivan"));
    }

    @Test
    @DisplayName("Создание профиля: имя и фамилия")
    void shouldCreateProfile_WithFullIdentity() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.empty());

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertEquals(2, savedProfile.getDetails().size());
        assertTrue(hasDetail(savedProfile, ProfileDetailType.FIRST_NAME, "Ivan"));
        assertTrue(hasDetail(savedProfile, ProfileDetailType.LAST_NAME, "Ivanov"));
    }

    @Test
    @DisplayName("Обновление профиля: актуализация данных и сохранение старых деталей")
    void shouldUpdateProfile_AddLastName_KeepExistingGithub() {
        Set<ProfileDetail> existingDetails = new HashSet<>();
        existingDetails.add(new ProfileDetail(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), "https://github.com/ivan"));
        existingDetails.add(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "OldName"));

        Profile existingProfile = Profile.builder()
                .id(1L)
                .telegramUserId(TELEGRAM_USER_ID)
                .details(existingDetails)
                .build();

        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("NewName")
                .lastName("NewSurname")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).save(profileCaptor.capture());
        Profile updated = profileCaptor.getValue();

        assertEquals(3, updated.getDetails().size());
        assertTrue(hasDetail(updated, ProfileDetailType.FIRST_NAME, "NewName"));
        assertTrue(hasDetail(updated, ProfileDetailType.LAST_NAME, "NewSurname"));
        assertTrue(hasDetail(updated, ProfileDetailType.GITHUB_PROFILE_URL, "https://github.com/ivan"));
    }

    @Test
    @DisplayName("Пропуск: событие равно null")
    void shouldDoNothing_WhenEventIsNull() {
        profileService.createOrUpdateProfile(null);
        verifyNoInteractions(profileRepository);
    }

    @Test
    @DisplayName("Пропуск: отсутствует telegramUserId")
    void shouldDoNothing_WhenTelegramUserIdIsNull() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(null)
                .firstName("Ivan")
                .build();

        profileService.createOrUpdateProfile(event);
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Пустые строки не должны добавляться в детали")
    void shouldNotAddDetails_WhenFieldsAreBlankStrings() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("   ")
                .lastName("")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.empty());

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertTrue(savedProfile.getDetails().isEmpty(), "Список деталей должен быть пустым для blank значений");
    }

    @Test
    @DisplayName("Обновление: если новые поля null, старые детали не должны затираться")
    void shouldNotOverwriteDataWithNulls_WhenUpdating() {
        Set<ProfileDetail> existingDetails = new HashSet<>();
        existingDetails.add(new ProfileDetail(ProfileDetailType.FIRST_NAME.getDetailName(), "Ivan"));

        Profile existingProfile = Profile.builder()
                .id(1L)
                .telegramUserId(TELEGRAM_USER_ID)
                .details(existingDetails)
                .build();

        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName(null)
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).save(profileCaptor.capture());
        Profile updated = profileCaptor.getValue();

        assertTrue(hasDetail(updated, ProfileDetailType.FIRST_NAME, "Ivan"));
    }

    private boolean hasDetail(Profile profile, ProfileDetailType type, String value) {
        if (profile.getDetails() == null) return false;
        return profile.getDetails().stream()
                .anyMatch(d -> d.getDetailName().equals(type.getDetailName()) && d.getDetailValue().equals(value));
    }
}