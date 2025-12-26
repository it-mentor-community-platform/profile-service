package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.SprinterAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SprinterAchievementStrategyTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private SprinterAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true когда 6 проектов выполнены за 5 месяцев")
    void checkCriteriaReturnsTrueWhenSixProjectsWithinFiveMonths() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long fiveMonths = 5L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + fiveMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает true, учитывая только обязательные проекты для определения 1-го и 6-го")
    void checkCriteriaConsidersOnlyRequiredProjectsForFirstAndSixth() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long fourMonths = 4L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.OTHER, baseTime),
                createProject(RoadmapProjectType.HANGMAN, baseTime + 1000),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 2000),
                createProject(RoadmapProjectType.TASK_TRACKER, baseTime + 3000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 4000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 5000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 6000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + fourMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает true, если есть дубликат последнего проекта, но один из них выполнен в пределах 6-ти месяцев")
    void checkCriteriaReturnsTrueWhenDuplicateExistsWithinSixMonths(){
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long fiveMonths = 5L * 30 * 24 * 60 * 60 * 1000;
        long sevenMonths = 7L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + fiveMonths ),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + sevenMonths )
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает true, если нужные проекты сделаны не по порядку и сделаны в переделах 6-ти месяцев")
    void checkCriteriaShouldReturnTrueIfOutProjectOrderAndLessSixthMonths() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long fourMonths = 4L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.SIMULATION, baseTime),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime+10000),
                createProject(RoadmapProjectType.HANGMAN, baseTime + 15000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 20000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 20000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + fourMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если разница между первым и последним проектом более 6-ти месяцев")
    void checkCriteriaShouldReturnFalseIfMoreThanSixthMonths() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long sevenMonths = 7L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + sevenMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false, если нужные проекты сделаны не по порядку и разница между первым и последним более 6-ти месяцев")
    void checkCriteriaShouldReturnFalseIfOutProjectOrderAndMoreThanSixthMonths() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long sevenMonths = 7L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.SIMULATION, baseTime),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime+10000),
                createProject(RoadmapProjectType.HANGMAN, baseTime + 15000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 20000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 20000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + sevenMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }


    @Test
    @DisplayName("Возвращает false, если есть дубликат одно из проектов, разница с которыми в пределах 6-ти месяцев и более 6-ти месяцев")
    void test(){
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long fiveMonths = 5L * 30 * 24 * 60 * 60 * 1000;
        long sevenMonths = 7L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.HANGMAN, baseTime+fiveMonths),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + sevenMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false, если сделаны только HANGMAN и CLOUD_FILE_STORAGE проекты в пределах 6-ти месяцев")
    void checkCriteriaShouldReturnFalseIfNotAllProjectsDone(){
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime+1000)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false когда разница ровно 6 месяцев")
    void checkCriteriaReturnsFalseWhenExactlySixMonthsDifference() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long sixMonths = 6L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + sixMonths)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false когда отсутствует один обязательный проект")
    void checkCriteriaReturnsFalseWhenMissingOneRequiredProject() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, 1000000L),
                createProject(RoadmapProjectType.SIMULATION, 1001000L),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, 1002000L),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, 1003000L),
                createProject(RoadmapProjectType.WEATHER_VIEWER, 1004000L)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false когда много проектов но разница больше 6 месяцев")
    void checkCriteriaReturnsFalseWhenManyProjectsButDifferenceMoreThanSixMonths() {
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;
        long sevenMonths = 7L * 30 * 24 * 60 * 60 * 1000;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime + sevenMonths),
                createProject(RoadmapProjectType.OTHER, baseTime + sevenMonths + 1000),
                createProject(RoadmapProjectType.TASK_TRACKER, baseTime + sevenMonths + 2000)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }


    @Test
    @DisplayName("Возвращает false, если 5 обязательных проектов и один нет")
    void checkCriteriaReturnsFalseWhenOneNotRequiredProject(){
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.HANGMAN, baseTime),
                createProject(RoadmapProjectType.SIMULATION, baseTime + 1000),
                createProject(RoadmapProjectType.CURRENCY_EXCHANGE, baseTime + 2000),
                createProject(RoadmapProjectType.TENNIS_SCOREBOARD, baseTime + 3000),
                createProject(RoadmapProjectType.WEATHER_VIEWER, baseTime + 4000),
                createProject(RoadmapProjectType.OTHER, baseTime + 6000)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    @Test
    @DisplayName("Возвращает false, не учитывая проекты, ненужные для ачивки SPRINTER")
    void checkCriteriaReturnsFalseWhenOnlyCloudAndOtherProjects(){
        Long userId = 228L;
        ProjectCreatedEvent event = new ProjectCreatedEvent();
        event.setAuthorTelegramUserId(userId);

        long baseTime = 1000000L;

        List<Project> projects = List.of(
                createProject(RoadmapProjectType.OTHER, baseTime),
                createProject(RoadmapProjectType.OTHER, baseTime+200),
                createProject(RoadmapProjectType.OTHER, baseTime+300),
                createProject(RoadmapProjectType.OTHER, baseTime+400),
                createProject(RoadmapProjectType.OTHER, baseTime+600),
                createProject(RoadmapProjectType.CLOUD_FILE_STORAGE, baseTime+900)
        );

        when(projectRepository.findByAuthorTelegramUserId(userId)).thenReturn(projects);

        boolean result = achievementStrategy.checkCriteria(event);

        assertFalse(result);
    }

    private Project createProject(RoadmapProjectType type, long timestamp) {
        Project project = new Project();
        project.setRoadmapProject(type);
        project.setProgrammingLanguage("Java");
        project.setAddedTimestamp(timestamp);
        return project;
    }
}

