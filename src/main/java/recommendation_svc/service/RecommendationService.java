package recommendation_svc.service;

import recommendation_svc.model.Recommendation;
import recommendation_svc.repository.RecommendationRepository;
import recommendation_svc.web.dto.RecommendationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RecommendationService {


    private final RecommendationRepository recommendationRepository;
    private final MailSender mailSender;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository, MailSender mailSender) {
        this.recommendationRepository = recommendationRepository;
        this.mailSender = mailSender;
    }

    public Recommendation sendRecommendation(RecommendationRequest recommendationRequest) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("sineadfly@gmail.com");
        message.setSubject("New Recommendation from " + recommendationRequest.getUserEmail());
        message.setText(recommendationRequest.getContent());

        Recommendation recommendation = Recommendation.builder()
                .userEmail(recommendationRequest.getUserEmail())
                .content(recommendationRequest.getContent())
                .createdOn(LocalDateTime.now())
                .build();

        try {
            mailSender.send(message);

        } catch (Exception e) {
            log.warn("Failed sending recommendation");
        }


        return recommendationRepository.save(recommendation);
    }

    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAllByOrderByCreatedOnDesc();
    }
}
