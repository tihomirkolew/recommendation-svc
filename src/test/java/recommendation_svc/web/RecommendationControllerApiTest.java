package recommendation_svc.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import recommendation_svc.model.Recommendation;
import recommendation_svc.service.RecommendationService;
import recommendation_svc.web.dto.RecommendationRequest;
import recommendation_svc.web.dto.RecommendationResponse;
import recommendation_svc.web.mapper.ResponseToRecommendationMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
public class RecommendationControllerApiTest {

    @MockitoBean
    private RecommendationService recommendationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestGetAllRecommendations_happyPath() throws Exception {

        // build
        Recommendation recommendation1 = Recommendation.builder()
                .userEmail("email@email.com")
                .content("Poor app.")
                .createdOn(LocalDateTime.now())
                .build();

        Recommendation recommendation2 = Recommendation.builder()
                .userEmail("email2@email2.com")
                .content("Great feature!")
                .createdOn(LocalDateTime.now())
                .build();

        // send
        when(recommendationService.getAllRecommendations()).thenReturn(List.of(recommendation1, recommendation2));
        MockHttpServletRequestBuilder request = get("/api/v1/recommendations");


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].userEmail", is(recommendation1.getUserEmail())))
                .andExpect(jsonPath("[0].content", is(recommendation1.getContent())))
                .andExpect(jsonPath("[1].userEmail", is(recommendation2.getUserEmail())))
                .andExpect(jsonPath("[1].content", is(recommendation2.getContent())));
    }

    @Test
    void testSendRecommendation() throws Exception {

        // build
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .userEmail("user@example.com")
                .content("This is a recommendation content")
                .build();
        
        Recommendation recommendation = Recommendation.builder()
                .userEmail("user@example.com")
                .content("This is a recommendation content")
                .createdOn(LocalDateTime.now())
                .build();

        RecommendationResponse recommendationResponse = ResponseToRecommendationMapper.fromRecommendation(recommendation);

        // send
        when(recommendationService.sendRecommendation(recommendationRequest)).thenReturn(recommendation);
        MockHttpServletRequestBuilder request = post("/api/v1/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(recommendationRequest));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userEmail").value(recommendationResponse.getUserEmail()))
                .andExpect(jsonPath("$.content").value(recommendationResponse.getContent()))
                .andExpect(jsonPath("$.createdOn").exists());
    }

    @Test
    void testArchiveRecommendation() throws Exception {

        // build
        UUID recommendationId = UUID.randomUUID();

        doNothing().when(recommendationService).archiveRecommendation(recommendationId);

        // send
        MockHttpServletRequestBuilder request = post("/api/v1/recommendations/{id}/archive", recommendationId);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
