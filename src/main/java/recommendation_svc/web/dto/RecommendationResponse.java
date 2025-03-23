package recommendation_svc.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationResponse {

    private String userEmail;

    private String content;

    private LocalDateTime createdOn;
}
