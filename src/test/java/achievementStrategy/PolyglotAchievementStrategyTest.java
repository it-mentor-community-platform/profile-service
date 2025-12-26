package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.PolyglotAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PolyglotAchievementStrategyTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private PolyglotAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если у юзера проекты на 4 различных языках")
    void shouldReturnTrueWhenUserHasFourDifferentLanguages() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        Set<String> fourLanguages = Set.of("Java", "Python", "JavaScript", "Go");

        when(projectRepository.findUsersProjectsLanguagesByUserId(userId))
                .thenReturn(fourLanguages);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает true, если у юзера проекты на 5 различных языках")
    void shouldReturnTrueWhenUserHasMoreThanFourLanguages() {

        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        Set<String> fiveLanguages = Set.of("Java", "Python", "JavaScript", "Go", "Rust");

        when(projectRepository.findUsersProjectsLanguagesByUserId(userId))
                .thenReturn(fiveLanguages);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если у юзера проекты на 3 различных языках")
    void shouldReturnFalseWhenUserHasLessThanFourLanguages() {

        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        Set<String> threeLanguages = Set.of("Java", "Python", "JavaScript");

        when(projectRepository.findUsersProjectsLanguagesByUserId(userId))
                .thenReturn(threeLanguages);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }
}
