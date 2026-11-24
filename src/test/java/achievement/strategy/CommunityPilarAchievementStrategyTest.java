package achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.CommunityPilarAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommunityPilarAchievementStrategyTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CommunityPilarAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если ревьюер имеет 100 ревью")
    void checkCriteriaShouldReturnTrueWhenReviewerHas_100_Reviews() {

        long countReviews = 100;
        Long userId = 228L;

        when(reviewRepository.count())
                .thenReturn(countReviews);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если ревьюер имеет менее 100 ревью")
    void checkCriteriaShouldReturnTrueWhenReviewerHasFewer_100_Reviews() {

        long countReviews = 99;
        Long userId = 228L;

        when(reviewRepository.count())
                .thenReturn(countReviews);

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }
}
