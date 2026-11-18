package service;

import com.itmentorcommunityplatform.profileservice.ProfileServiceApplication;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.exception.ProfileAlreadyExistException;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.service.ProfilePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("ide")
@SpringBootTest(classes = ProfileServiceApplication.class)
@Testcontainers
class ProfilePersistenceServiceTest {

    private static final Long TEST_TELEGRAM_USER_ID = 111L;
    private static final String TEST_GITHUB_URL = "https://github.com/ivan";
    private static final String TEST_FIRST_NAME = "Ivan";
    private static final String TEST_TELEGRAM_URL = "https://t.me/ivan";

    private static final String FIRST_NAME = ProfileDetailType.FIRST_NAME.getDetailName();
    private static final String LAST_NAME = ProfileDetailType.LAST_NAME.getDetailName();
    private static final String GITHUB_URL = ProfileDetailType.GITHUB_PROFILE_URL.getDetailName();
    private static final String TELEGRAM_URL = ProfileDetailType.TELEGRAM_URL.getDetailName();

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
                    .withDatabaseName("profile_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProfilePersistenceService profilePersistenceService;

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание профиля")
    void shouldCreateProfile() {
        Map<String, String> details = Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL);
        Profile result = profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, details);
        assertNotNull(result.getId());
        assertEquals(TEST_TELEGRAM_USER_ID, result.getTelegramUserId());
        assertEquals(Set.of(new ProfileDetail(FIRST_NAME, TEST_FIRST_NAME), new ProfileDetail(GITHUB_URL, TEST_GITHUB_URL)),
                result.getDetails());
        Profile savedProfile = profileRepository
                .findByTelegramUserId(TEST_TELEGRAM_USER_ID)
                .orElseThrow();
        assertEquals(result.getId(), savedProfile.getId());
        assertEquals(result.getDetails(), savedProfile.getDetails());
    }

    @Test
    @DisplayName("Создание профиля: занятый GitHub URL пропускается")
    void shouldSkipGithubUrl_WhenItAlreadyExists() {
        profilePersistenceService.createProfile(222L, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        assertNotNull(result.getId());
        assertEquals(Set.of(new ProfileDetail(FIRST_NAME, TEST_FIRST_NAME)), result.getDetails());
    }

    @Test
    @DisplayName("Создание профиля: занятый Telegram URL пропускается")
    void shouldSkipTelegramUrl_WhenItAlreadyExists() {
        profilePersistenceService.createProfile(222L, Map.of(TELEGRAM_URL, TEST_TELEGRAM_URL));
        Profile result = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, TELEGRAM_URL, TEST_TELEGRAM_URL));
        assertNotNull(result.getId());
        assertEquals(Set.of(new ProfileDetail(FIRST_NAME, TEST_FIRST_NAME)), result.getDetails());
    }

    @Test
    @DisplayName("Создание профиля: Telegram ID уже занят")
    void shouldThrowException_WhenTelegramIdAlreadyExists() {
        profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME));
        assertThrows(ProfileAlreadyExistException.class,
                () -> profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, "Petr")));
        assertEquals(1, profileRepository.count());
    }

    @Test
    @DisplayName("Обновление профиля: новые детали добавляются, старые сохраняются")
    void shouldUpdateProfile_WithoutDeletingExistingDetails() {
        profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService
                .updateProfile(TEST_TELEGRAM_USER_ID, Map.of(LAST_NAME, "Ivanov", FIRST_NAME, "Petr"));
        assertEquals(Set.of(new ProfileDetail(FIRST_NAME, "Petr"), new ProfileDetail(LAST_NAME, "Ivanov"),
                new ProfileDetail(GITHUB_URL, TEST_GITHUB_URL)), result.getDetails());
    }

    @Test
    @DisplayName("Обновление профиля: занятый GitHub URL пропускается")
    void shouldSkipGithubUrl_WhenItBelongsToAnotherProfile() {
        profilePersistenceService.createProfile(222L, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME));
        Profile result = profilePersistenceService
                .updateProfile(TEST_TELEGRAM_USER_ID, Map.of(GITHUB_URL, TEST_GITHUB_URL, LAST_NAME, "Ivanov"));
        assertEquals(Set.of(new ProfileDetail(FIRST_NAME, TEST_FIRST_NAME),
                new ProfileDetail(LAST_NAME, "Ivanov")), result.getDetails());
    }

    @Test
    @DisplayName("Обновление профиля: собственный GitHub URL не считается конфликтом")
    void shouldAllowOwnGithubUrl_WhenUpdatingProfile() {
        profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID,
                Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService
                .updateProfile(TEST_TELEGRAM_USER_ID, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        assertTrue(result.getDetails().contains(new ProfileDetail(GITHUB_URL, TEST_GITHUB_URL)));
        assertEquals(2, result.getDetails().size());
    }
}