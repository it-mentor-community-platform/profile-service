package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.BilingualAchievementStrategy;
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
public class BilingualAchievementStrategyTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private BilingualAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true при проектах на 2 разных языках")
    void checkCriteriaShouldReturnTrueWhenUserHasProjectsOnTwoLanguages() {

        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        Set<String> twoLanguages = Set.of("Java", "Python");

        when(projectRepository.findUsersProjectsLanguagesByUserId(userId))
                .thenReturn(twoLanguages);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false при проектах только на 1 языке")
    void checkCriteriaShouldReturnTrueWhenUserHasProjectsOnOnlyOneLanguage() {

        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        Set<String> twoLanguages = Set.of("Java");

        when(projectRepository.findUsersProjectsLanguagesByUserId(userId))
                .thenReturn(twoLanguages);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false при проектах на 3 языках")
    void checkCriteriaShouldReturnFalseWhenUserHasProjectsOnThreeOrMoreLanguages() {

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
