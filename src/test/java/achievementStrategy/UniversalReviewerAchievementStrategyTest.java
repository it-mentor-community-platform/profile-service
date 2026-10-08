package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.UniversalReviewerAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniversalReviewerAchievementStrategyTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private UniversalReviewerAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если ревьюер имеет ревью на проекты более чем 4 разных типов")
    void checkCriteriaShouldReturnTrueWhenReviewerHasReviewersForMoreThan_4_Projects() {

        Long userId = 228L;

        Set<RoadmapProjectType> userRoadmapProjects =  EnumSet.allOf(RoadmapProjectType.class)
                .stream()
                .filter(project -> project != RoadmapProjectType.OTHER)
                .filter(project -> project != RoadmapProjectType.SIMULATION)
                .collect(Collectors.toSet());

        when(projectRepository.findProjectsNamesByUserId(userId))
                .thenReturn(userRoadmapProjects);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если ревьюер имеет ревью на проекты менее чем 4 разных типов")
    void checkCriteriaShouldReturnTrueWhenReviewerHasReviewersForFewerThan_4_Projects() {

        Long userId = 228L;

        Set<RoadmapProjectType> userRoadmapProjects =  EnumSet.allOf(RoadmapProjectType.class)
                .stream()
                .filter(project -> project != RoadmapProjectType.OTHER)
                .filter(project -> project != RoadmapProjectType.SIMULATION)
                .filter(project -> project != RoadmapProjectType.HANGMAN)
                .filter(project -> project != RoadmapProjectType.CURRENCY_EXCHANGE)
                .filter(project -> project != RoadmapProjectType.WEATHER_VIEWER)
                .collect(Collectors.toSet());

        when(projectRepository.findProjectsNamesByUserId(userId))
                .thenReturn(userRoadmapProjects);


        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }
}
