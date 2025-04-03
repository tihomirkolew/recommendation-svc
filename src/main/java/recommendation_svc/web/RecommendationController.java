package recommendation_svc.web;

import recommendation_svc.model.Recommendation;
import recommendation_svc.service.RecommendationService;
import recommendation_svc.web.dto.RecommendationRequest;
import recommendation_svc.web.dto.RecommendationResponse;
import recommendation_svc.web.mapper.ResponseToRecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<RecommendationResponse> sendRecommendation(@RequestBody RecommendationRequest recommendationRequest) {

        Recommendation recommendation = recommendationService.sendRecommendation(recommendationRequest);

        RecommendationResponse response = ResponseToRecommendationMapper.fromRecommendation(recommendation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<Recommendation>> getAllRecommendations() {
        return ResponseEntity.ok(recommendationService.getAllRecommendations());
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveRecommendation(@PathVariable UUID id) {
        recommendationService.archiveRecommendation(id);
        return ResponseEntity.noContent().build();
    }
}
