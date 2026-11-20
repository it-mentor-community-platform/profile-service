package service;

import com.itmentorcommunityplatform.profileservice.ProfileServiceApplication;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.repository.ProfileDetailRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.service.ProfilePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Import(ProfilePersistenceService.class)
@ContextConfiguration(classes = ProfileServiceApplication.class)
@Testcontainers
@ActiveProfiles("ide")
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

    @Autowired
    private ProfileDetailRepository profileDetailRepository;

    @BeforeEach
    void setUp() {
        profileDetailRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание профиля")
    void shouldCreateProfile() {
        Map<String, String> details = Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL);
        Profile result = profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, details);
        assertNotNull(result.getId());
        assertEquals(TEST_TELEGRAM_USER_ID, result.getTelegramUserId());
        assertDetails(result, details);
        Profile savedProfile = profileRepository
                .findByTelegramUserId(TEST_TELEGRAM_USER_ID)
                .orElseThrow();
        assertEquals(result.getId(), savedProfile.getId());
        Set<ProfileDetail> savedDetails = profileDetailRepository.findAllByProfileId(savedProfile.getId());
        assertDetails(savedDetails, details);
    }

    @Test
    @DisplayName("Создание профиля: занятый GitHub URL пропускается")
    void shouldSkipGithubUrl_WhenItAlreadyExists() {
        profilePersistenceService.createProfile(222L, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        assertNotNull(result.getId());
        assertDetails(result, Map.of(FIRST_NAME, TEST_FIRST_NAME));
    }

    @Test
    @DisplayName("Создание профиля: занятый Telegram URL пропускается")
    void shouldSkipTelegramUrl_WhenItAlreadyExists() {
        profilePersistenceService.createProfile(222L, Map.of(TELEGRAM_URL, TEST_TELEGRAM_URL));
        Profile result = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, TELEGRAM_URL, TEST_TELEGRAM_URL));
        assertNotNull(result.getId());
        assertDetails(result, Map.of(FIRST_NAME, TEST_FIRST_NAME));
    }

    @Test
    @DisplayName("Создание профиля: Telegram ID уже занят")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowException_WhenTelegramIdAlreadyExists() {
        profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME));
        assertThrows(DbActionExecutionException.class,
                () -> profilePersistenceService.createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, "Petr")));
        assertEquals(1, profileRepository.count());
    }

    @Test
    @DisplayName("Обновление профиля: новые детали добавляются, старые сохраняются")
    void shouldUpdateProfile_WithoutDeletingExistingDetails() {
        Profile profile = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService.updateProfile(profile, Map.of(LAST_NAME, "Ivanov", FIRST_NAME, "Petr"));
        assertDetails(result, Map.of(FIRST_NAME, "Petr", LAST_NAME, "Ivanov", GITHUB_URL, TEST_GITHUB_URL));
    }

    @Test
    @DisplayName("Обновление профиля: занятый GitHub URL пропускается")
    void shouldSkipGithubUrl_WhenItBelongsToAnotherProfile() {
        profilePersistenceService.createProfile(222L, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        Profile profile = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME));
        Profile result = profilePersistenceService
                .updateProfile(profile, Map.of(GITHUB_URL, TEST_GITHUB_URL, LAST_NAME, "Ivanov"));
        assertDetails(result, Map.of(FIRST_NAME, TEST_FIRST_NAME, LAST_NAME, "Ivanov"));
    }

    @Test
    @DisplayName("Обновление профиля: собственный GitHub URL не считается конфликтом")
    void shouldAllowOwnGithubUrl_WhenUpdatingProfile() {
        Profile profile = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
        Profile result = profilePersistenceService.updateProfile(profile, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        assertDetails(result, Map.of(FIRST_NAME, TEST_FIRST_NAME, GITHUB_URL, TEST_GITHUB_URL));
    }

    @Test
    @DisplayName("Обновление профиля: GitHub URL актуализируется")
    void shouldUpdateGithubUrl_WhenNewUrlIsNotOccupied() {
        Profile profile = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(GITHUB_URL, TEST_GITHUB_URL));
        String newGithubUrl = "https://github.com/new-ivan";
        Profile result = profilePersistenceService.updateProfile(profile, Map.of(GITHUB_URL, newGithubUrl));
        assertDetails(result, Map.of(GITHUB_URL, newGithubUrl));
    }

    @Test
    @DisplayName("Обновление профиля: Telegram URL актуализируется")
    void shouldUpdateTelegramUrl_WhenNewUrlIsNotOccupied() {
        Profile profile = profilePersistenceService
                .createProfile(TEST_TELEGRAM_USER_ID, Map.of(TELEGRAM_URL, TEST_TELEGRAM_URL));
        String newTelegramUrl = "https://t.me/new_ivan";
        Profile result = profilePersistenceService.updateProfile(profile, Map.of(TELEGRAM_URL, newTelegramUrl));
        assertDetails(result, Map.of(TELEGRAM_URL, newTelegramUrl));
    }

    private void assertDetails(Profile profile, Map<String, String> expectedDetails) {
        assertDetails(profile.getDetails(), expectedDetails);
    }

    private void assertDetails(Set<ProfileDetail> actualDetails,
                               Map<String, String> expectedDetails) {
        Map<String, String> actual = actualDetails.stream()
                .collect(Collectors.toMap(
                        ProfileDetail::getDetailName,
                        ProfileDetail::getDetailValue
                ));

        assertEquals(expectedDetails, actual);
    }
}