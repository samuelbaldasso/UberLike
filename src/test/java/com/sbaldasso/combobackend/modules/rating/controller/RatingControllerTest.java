package com.sbaldasso.combobackend.modules.rating.controller;

import com.sbaldasso.combobackend.modules.rating.dto.CreateRatingRequest;
import com.sbaldasso.combobackend.modules.rating.dto.RatingResponse;
import com.sbaldasso.combobackend.modules.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingControllerTest {
    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRating_returnsRatingResponse() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        CreateRatingRequest request = CreateRatingRequest.builder()
                .rating(5)
                .comment("Excelente serviço!")
                .build();

        RatingResponse expectedResponse = RatingResponse.builder()
                .id(UUID.randomUUID())
                .rating(5)
                .comment("Excelente serviço!")
                .build();

        when(ratingService.createRating(deliveryId, raterId, request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<RatingResponse> response = ratingController.createRating(deliveryId, raterId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(ratingService).createRating(deliveryId, raterId, request);
    }

    @Test
    void getUserRatings_returnsPageOfRatings() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Page<RatingResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(ratingService.getUserRatings(userId, Pageable.unpaged())).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<RatingResponse>> response = ratingController.getUserRatings(userId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(ratingService).getUserRatings(userId, Pageable.unpaged());
    }

    @Test
    void getAverageRating_returnsRatingAverage() {
        // Arrange
        UUID userId = UUID.randomUUID();
        double expectedAverage = 4.5;
        when(ratingService.getUserAverageRating(userId)).thenReturn(expectedAverage);

        // Act
        ResponseEntity<Double> response = ratingController.getAverageRating(userId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedAverage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(ratingService).getUserAverageRating(userId);
    }

    @Test
    void getDeliveryRatings_returnsRatingResponses() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        RatingResponse driverRating = RatingResponse.builder()
                .id(UUID.randomUUID())
                .rating(5)
                .comment("Ótimo motorista")
                .build();
        RatingResponse customerRating = RatingResponse.builder()
                .id(UUID.randomUUID())
                .rating(4)
                .comment("Bom cliente")
                .build();

        when(ratingService.getDeliveryRatings(deliveryId))
                .thenReturn(new RatingResponse[]{driverRating, customerRating});

        // Act
        ResponseEntity<RatingResponse[]> response = ratingController.getDeliveryRatings(deliveryId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertEquals(200, response.getStatusCodeValue());
        verify(ratingService).getDeliveryRatings(deliveryId);
    }

    @Test
    void reportRating_returnsOkResponse() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        String reason = "Comentário inadequado";

        // Act
        ResponseEntity<Void> response = ratingController.reportRating(ratingId, reporterId, reason);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(ratingService).reportRating(ratingId, reporterId, reason);
    }
}
