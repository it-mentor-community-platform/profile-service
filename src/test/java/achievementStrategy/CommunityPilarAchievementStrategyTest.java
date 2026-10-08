package achievementStrategy;

import com.itmentorcommunityplatform.profileservice.domain.Review;
import com.itmentorcommunityplatform.profileservice.domain.achievement.strategy.CommunityPilarAchievementStrategy;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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

        int countReviews = 100;
        Long userId = 228L;

        when(reviewRepository.findByReviewerTelegramUserId(userId))
                .thenReturn(getReviews(userId, countReviews));

        boolean result = achievementStrategy.checkCriteria(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Возвращает false, если ревьюер имеет менее 100 ревью")
    void checkCriteriaShouldReturnTrueWhenReviewerHasFewer_100_Reviews() {

        int countReviews = 99;
        Long userId = 228L;

        when(reviewRepository.findByReviewerTelegramUserId(userId))
                .thenReturn(getReviews(userId, countReviews));

        boolean result = achievementStrategy.checkCriteria(userId);

        assertFalse(result);
    }

    List<Review> getReviews(Long userId, int countReviews) {
        List<Review> reviews = new ArrayList<>();

        for (int i = 0; i < countReviews; i++) {
            reviews.add(new Review(
                    1L + i,
                    1L + i,
                    userId,
                    null,
                    null
            ));
        }

        return reviews;
    }
}
