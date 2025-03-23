package recommendation_svc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RecommendationRequest {

    @NotNull
    @Email
    String userEmail;


    @NotNull
    @Size(max = 500)
    String content;
}
