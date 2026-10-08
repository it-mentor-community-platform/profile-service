package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.Review;
import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.FutureMentorAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FutureMentorAchievementStrategyTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private FutureMentorAchievementStrategy achievementStrategy;

    @Test
    @DisplayName("Возвращает true, если ревьюер сдал своё первое ревью")
    void checkCriteriaShouldReturnTrueWhenReviewerHasFirstReview() {

        Long userId = 228L;

        when(reviewRepository.findByReviewerTelegramUserId(userId))
                .thenReturn(List.of(Review.builder().id(1L).projectId(userId).url(null).timestamp(null).build()));

        boolean result = achievementStrategy.checkCriteria(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если ревьюер не сдал ни одного ревью")
    void checkCriteriaShouldReturnTFalseWhenReviewerNotHasFirstReview() {

        Long userId = 228L;

        when(reviewRepository.findByReviewerTelegramUserId(userId))
                .thenReturn(List.of());

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }
}