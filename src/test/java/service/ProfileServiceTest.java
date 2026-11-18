package service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.event.UserCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.service.ProfilePersistenceService;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfilePersistenceService profilePersistenceService;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsCaptor;

    private final Long TELEGRAM_USER_ID = 111L;

    @Test
    @DisplayName("Создание профиля: только имя")
    void shouldCreateProfile_WithOnlyFirstName() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("Ivan")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.empty());

        when(profilePersistenceService.createProfile(eq(TELEGRAM_USER_ID), anyMap()))
                .thenReturn(new Profile(TELEGRAM_USER_ID, Set.of()));

        profileService.createOrUpdateProfile(event);

        verify(profilePersistenceService).createProfile(eq(TELEGRAM_USER_ID), detailsCaptor.capture());

        assertEquals(Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), "Ivan"), detailsCaptor.getValue());

        verify(profilePersistenceService, never()).updateProfile(any(), any());
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

        when(profilePersistenceService.createProfile(eq(TELEGRAM_USER_ID), anyMap()))
                .thenReturn(new Profile(TELEGRAM_USER_ID, Set.of()));

        profileService.createOrUpdateProfile(event);

        verify(profilePersistenceService).createProfile(eq(TELEGRAM_USER_ID), detailsCaptor.capture());

        assertEquals(
                Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), "Ivan",
                        ProfileDetailType.LAST_NAME.getDetailName(), "Ivanov"),
                detailsCaptor.getValue());
    }

    @Test
    @DisplayName("Обновление профиля: актуализация данных - должны измениться лишь новые данные")
    void shouldUpdateProfile_WithOnlyIncomingDetails() {
        Profile existingProfile = new Profile(1L, TELEGRAM_USER_ID, new HashSet<>());

        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName("NewName")
                .lastName("NewSurname")
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));

        profileService.createOrUpdateProfile(event);

        verify(profileRepository).findByTelegramUserId(TELEGRAM_USER_ID);

        verify(profilePersistenceService).updateProfile(eq(TELEGRAM_USER_ID), detailsCaptor.capture());

        assertEquals(Map.of(ProfileDetailType.FIRST_NAME.getDetailName(), "NewName",
                        ProfileDetailType.LAST_NAME.getDetailName(), "NewSurname"
                ), detailsCaptor.getValue());

        verify(profilePersistenceService, never()).createProfile(any(), any());
    }

    @Test
    @DisplayName("Пропуск: событие равно null")
    void shouldDoNothing_WhenEventIsNull() {
        profileService.createOrUpdateProfile(null);
        verifyNoInteractions(profileRepository, profilePersistenceService);
    }

    @Test
    @DisplayName("Пропуск: отсутствует telegramUserId")
    void shouldDoNothing_WhenTelegramUserIdIsNull() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(null)
                .firstName("Ivan")
                .build();

        profileService.createOrUpdateProfile(event);
        verifyNoInteractions(profileRepository, profilePersistenceService);
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

        when(profilePersistenceService.createProfile(eq(TELEGRAM_USER_ID), anyMap()))
                .thenReturn(new Profile(TELEGRAM_USER_ID, Set.of()));

        profileService.createOrUpdateProfile(event);

        verify(profilePersistenceService).createProfile(eq(TELEGRAM_USER_ID), detailsCaptor.capture());

        assertTrue(detailsCaptor.getValue().isEmpty(), "Карта деталей должна быть пустой для blank значений");
    }

    @Test
    @DisplayName("Обновление: если новые поля null, они и не будут переданы")
    void shouldNotOverwriteDataWithNulls_WhenUpdating() {
        Profile existingProfile = new Profile(1L, TELEGRAM_USER_ID, new HashSet<>());

        UserCreatedEvent event = UserCreatedEvent.builder()
                .telegramUserId(TELEGRAM_USER_ID)
                .firstName(null)
                .build();

        when(profileRepository.findByTelegramUserId(TELEGRAM_USER_ID)).thenReturn(Optional.of(existingProfile));

        profileService.createOrUpdateProfile(event);

        verify(profilePersistenceService).updateProfile(eq(TELEGRAM_USER_ID), detailsCaptor.capture());

        assertTrue(detailsCaptor.getValue().isEmpty(), "Null-значения не должны попадать в карту деталей");
    }
}