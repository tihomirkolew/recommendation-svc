package recommendation_svc.web.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import recommendation_svc.model.Recommendation;
import recommendation_svc.web.dto.RecommendationResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ResponseToRecommendationMapperUnitTest {


    @Test
    void testFromRecommendationMapperSuccessfullyMapsRecommendationToResponse() {

        // given
        Recommendation recommendation = Recommendation.builder()
                .userEmail("email@email.com")
                .content("I suggest you moderate your app more!")
                .createdOn(LocalDateTime.now())
                .build();

        // when
        RecommendationResponse response =
                ResponseToRecommendationMapper.fromRecommendation(recommendation);

        // then
        assertNotNull(response);
        assertEquals(recommendation.getUserEmail(), response.getUserEmail());
        assertEquals(recommendation.getContent(), response.getContent());
        assertEquals(recommendation.getCreatedOn(), response.getCreatedOn());
    }
}
