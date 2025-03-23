package recommendation_svc.web.mapper;

import recommendation_svc.model.Recommendation;
import recommendation_svc.web.dto.RecommendationResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static RecommendationResponse fromRecommendation(Recommendation recommendation) {

        return RecommendationResponse.builder()
                .userEmail(recommendation.getUserEmail())
                .content(recommendation.getContent())
                .createdOn(recommendation.getCreatedOn())
                .build();
    }
}
