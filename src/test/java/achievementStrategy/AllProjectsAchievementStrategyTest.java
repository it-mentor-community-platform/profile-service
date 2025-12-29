package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.AllProjectsAchievementStrategy;
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
public class AllProjectsAchievementStrategyTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private AllProjectsAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если юзер выполнил 7 проектов")
    void checkCriteriaShouldReturnTrueWhenUserHasAllRequiredProjects() {

        Long userId = 228L;

        Set<RoadmapProjectType> userRoadmapProjects =  EnumSet.allOf(RoadmapProjectType.class)
                .stream()
                .filter(project -> project != RoadmapProjectType.OTHER)
                .collect(Collectors.toSet());

        when(projectRepository.findProjectsNamesByUserId(userId))
                .thenReturn(userRoadmapProjects);


        boolean result = achievementStrategy.checkCriteria(userId);


        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если юзер выполнил 7 проектов и проект не из роадмапа")
    void checkCriteriaShouldReturnTrueWhenUserHasAllRequiredProjectsAndOther() {

        Long userId = 228L;

        Set<RoadmapProjectType> userRoadmapProjects =  EnumSet.allOf(RoadmapProjectType.class);

        when(projectRepository.findProjectsNamesByUserId(userId))
                .thenReturn(userRoadmapProjects);

        boolean result = achievementStrategy.checkCriteria(userId);


        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если юзер не выполнил 7 проектов")
    void checkCriteriaShouldReturnFalseWhenUserMissingOneRequiredProjects(){
        Long userId = 228L;

        Set<RoadmapProjectType> userRoadmapProjects =  EnumSet.allOf(RoadmapProjectType.class)
                .stream().filter(project -> project != RoadmapProjectType.OTHER && project != RoadmapProjectType.TASK_TRACKER)
                .collect(Collectors.toSet());

        when(projectRepository.findProjectsNamesByUserId(userId))
                .thenReturn(userRoadmapProjects);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }
}
