package recommendation_svc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import recommendation_svc.model.Recommendation;
import recommendation_svc.repository.RecommendationRepository;
import recommendation_svc.web.dto.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceUnitTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void sendRecommendation_shouldSendEmailAndSaveRecommendation() {

        // given
        RecommendationRequest request = RecommendationRequest.builder()
                .userEmail("user1@user.user")
                .content("Great service!")
                .build();

        Recommendation savedRecommendation = Recommendation.builder()
                .userEmail("email@email.com")
                .content("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        // when
        Recommendation result = recommendationService.sendRecommendation(request);

        // then
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo("sineadfly@gmail.com");
        expectedMessage.setSubject("New Recommendation from user1@user.user");
        expectedMessage.setText("Great service!");

        verify(mailSender, times(1)).send(refEq(expectedMessage));
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        assertThat(result).isEqualTo(savedRecommendation);
    }

    @Test
    void sendRecommendation_whenMailSenderFails_shouldLogWarningAndSaveRecommendation() {

        // given
        RecommendationRequest request = RecommendationRequest.builder()
                .userEmail("user1@user.user")
                .content("Great service!")
                .build();

        Recommendation savedRecommendation = Recommendation.builder()
                .userEmail("test@example.com")
                .content("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // when
        Recommendation result = recommendationService.sendRecommendation(request);

        // then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        assertThat(result).isEqualTo(savedRecommendation);
    }

    @Test
    void getAllRecommendations_shouldReturnRecommendationListOrderedByCreationDesc() {

        // given
        Recommendation savedRecommendation1 = Recommendation.builder()
                .userEmail("email@email.com")
                .content("Great service!")
                .createdOn(LocalDateTime.now().minusMinutes(15))
                .build();

        Recommendation savedRecommendation2 = Recommendation.builder()
                .userEmail("email2@email2.com")
                .content("I was scammed.")
                .createdOn(LocalDateTime.now())
                .build();

        when(recommendationRepository.findAllByOrderByArchivedAscCreatedOnDesc())
                .thenReturn(List.of(savedRecommendation2, savedRecommendation1));

        // when
        List<Recommendation> allRecommendations = recommendationService.getAllRecommendations();

        // then
        assertNotNull(allRecommendations);
        assertEquals(allRecommendations.get(0), savedRecommendation2);
        assertEquals(allRecommendations.get(1), savedRecommendation1);
        verify(recommendationRepository, times(1)).findAllByOrderByArchivedAscCreatedOnDesc();
    }

    @Test
    void archiveRecommendation_happyPath () {

        // given
        UUID id = UUID.randomUUID();
        Recommendation mockRecommendation = Recommendation.builder()
                .id(id)
                .archived(false)
                .build();

        when(recommendationRepository.findById(id)).thenReturn(Optional.of(mockRecommendation));

        // when
        recommendationService.archiveRecommendation(id);

        // then
        verify(recommendationRepository).findById(id);
        verify(recommendationRepository).save(mockRecommendation);

        assertTrue(mockRecommendation.isArchived());
    }

}
