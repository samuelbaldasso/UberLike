package com.sbaldasso.combobackend.modules.rating.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.rating.domain.Rating;
import com.sbaldasso.combobackend.modules.rating.dto.CreateRatingRequest;
import com.sbaldasso.combobackend.modules.rating.dto.RatingResponse;
import com.sbaldasso.combobackend.modules.rating.repository.RatingRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingService ratingService;

    private User customer;
    private User driver;
    private Delivery delivery;
    private Rating rating;
    private CreateRatingRequest request;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(UUID.randomUUID());
        customer.setUserType(UserType.CUSTOMER);

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUserType(UserType.DRIVER);

        delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setCustomer(customer);
        delivery.setDriver(driver);
        delivery.setFinished(true);

        rating = new Rating();
        rating.setId(UUID.randomUUID());
        rating.setDelivery(delivery);
        rating.setReviewer(customer);
        rating.setRatedUser(driver);
        rating.setRating(5);
        rating.setComment("Ótimo serviço");

        request = new CreateRatingRequest();
        request.setRating(5);
        request.setComment("Ótimo serviço");
    }

    @Test
    void createRating_CustomerRatingDriver_Success() {
        // Arrange
        when(ratingRepository.findByDeliveryIdAndReviewerId(delivery.getId(), customer.getId()))
                .thenReturn(Optional.empty());
        when(deliveryService.getDeliveryById(delivery.getId())).thenReturn(delivery);
        when(userService.validateAndGetUser(customer.getId())).thenReturn(customer);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        RatingResponse response = ratingService.createRating(delivery.getId(), customer.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals(rating.getId(), response.getId());
        assertEquals(rating.getRating(), response.getRating());
        assertEquals(rating.getComment(), response.getComment());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void createRating_DuplicateRating_ThrowsException() {
        // Arrange
        when(ratingRepository.findByDeliveryIdAndReviewerId(delivery.getId(), customer.getId()))
                .thenReturn(Optional.of(rating));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> ratingService.createRating(delivery.getId(), customer.getId(), request));
    }

    @Test
    void createRating_UnfinishedDelivery_ThrowsException() {
        // Arrange
        delivery.setFinished(false);
        when(ratingRepository.findByDeliveryIdAndReviewerId(delivery.getId(), customer.getId()))
                .thenReturn(Optional.empty());
        when(deliveryService.getDeliveryById(delivery.getId())).thenReturn(delivery);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> ratingService.createRating(delivery.getId(), customer.getId(), request));
    }

    @Test
    void getUserRatings_Success() {
        // Arrange
        Page<Rating> ratingPage = new PageImpl<>(Collections.singletonList(rating));
        when(ratingRepository.findByRatedUserId(driver.getId(), Pageable.unpaged()))
                .thenReturn(ratingPage);

        // Act
        Page<RatingResponse> response = ratingService.getUserRatings(driver.getId(), Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(rating.getId(), response.getContent().get(0).getId());
    }

    @Test
    void getUserAverageRating_Success() {
        // Arrange
        when(ratingRepository.getAverageRatingForUser(driver.getId())).thenReturn(4.5);

        // Act
        double average = ratingService.getUserAverageRating(driver.getId());

        // Assert
        assertEquals(4.5, average);
    }

    @Test
    void getUserRatingCount_Success() {
        // Arrange
        when(ratingRepository.countRatingsForUser(driver.getId())).thenReturn(10L);

        // Act
        long count = ratingService.getUserRatingCount(driver.getId());

        // Assert
        assertEquals(10L, count);
    }
}
