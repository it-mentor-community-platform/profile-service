package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.FirstProjectAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirstProjectAchievementStrategyTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private FirstProjectAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если это первый проект юзера")
    void shouldReturnTrueWhenUserHasExactlyOneProject() {
        Long userId = 228L;

        List<Project> singleProject = List.of(
                new Project(1L, userId, null, "Java", RoadmapProjectType.SIMULATION, null)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId))
                .thenReturn(singleProject);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если у юзера проектов больше, чем 1")
    void shouldReturnFalseWhenUserHasMoreThanOneProject() {
        Long userId = 228L;

        List<Project> singleProject = List.of(
                new Project(1L, userId, null, "Java", RoadmapProjectType.SIMULATION, null),
                new Project(2L, userId, null, "Java", RoadmapProjectType.HANGMAN, null)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId))
                .thenReturn(singleProject);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }


    @Test
    @DisplayName("Возвращает false, если у юзера нет проектов")
    void shouldReturnFalseWhenUserHasNoProjects() {

        Long userId = 228L;

        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        when(projectRepository.findByAuthorTelegramUserId(userId))
                .thenReturn(Collections.emptyList());

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }
}
