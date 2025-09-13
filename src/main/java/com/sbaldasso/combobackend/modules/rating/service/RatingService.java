package com.sbaldasso.combobackend.modules.rating.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.rating.domain.Rating;
import com.sbaldasso.combobackend.modules.rating.dto.CreateRatingRequest;
import com.sbaldasso.combobackend.modules.rating.dto.RatingResponse;
import com.sbaldasso.combobackend.modules.rating.repository.RatingRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final DeliveryService deliveryService;
    private final UserService userService;

    @Transactional
    public RatingResponse createRating(UUID deliveryId, UUID reviewerId, CreateRatingRequest request) {
        // Verifica se já existe avaliação deste usuário para esta entrega
        if (ratingRepository.findByDeliveryIdAndReviewerId(deliveryId, reviewerId).isPresent()) {
            throw new IllegalStateException("Usuário já avaliou esta entrega");
        }

        Delivery delivery = deliveryService.getDeliveryById(deliveryId);
        
        // Verifica se a entrega está finalizada
        if (!delivery.isFinished()) {
            throw new IllegalStateException("Só é possível avaliar entregas finalizadas");
        }

        // Determina quem está sendo avaliado
        User reviewer = userService.validateAndGetUser(reviewerId);
        User ratedUser = determineRatedUser(delivery, reviewer);

        Rating rating = new Rating();
        rating.setDelivery(delivery);
        rating.setReviewer(reviewer);
        rating.setRatedUser(ratedUser);
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());

        rating = ratingRepository.save(rating);
        return toRatingResponse(rating);
    }

    private User determineRatedUser(Delivery delivery, User reviewer) {
        // Se o avaliador é o cliente, está avaliando o motorista
        if (reviewer.getId().equals(delivery.getCustomer().getId())) {
            return delivery.getDriver();
        }
        // Se o avaliador é o motorista, está avaliando o cliente
        else if (reviewer.getId().equals(delivery.getDriver().getId())) {
            return delivery.getCustomer();
        }
        throw new IllegalStateException("Avaliador deve ser o cliente ou o motorista da entrega");
    }

    @Transactional(readOnly = true)
    public RatingResponse getRating(UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));
        return toRatingResponse(rating);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> getUserRatings(UUID userId, Pageable pageable) {
        return ratingRepository.findByRatedUserId(userId, pageable)
                .map(this::toRatingResponse);
    }

    @Transactional(readOnly = true)
    public double getUserAverageRating(UUID userId) {
        Double average = ratingRepository.getAverageRatingForUser(userId);
        return average != null ? average : 0.0;
    }

    @Transactional(readOnly = true)
    public long getUserRatingCount(UUID userId) {
        return ratingRepository.countRatingsForUser(userId);
    }

    private RatingResponse toRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .deliveryId(rating.getDelivery().getId())
                .reviewerId(rating.getReviewer().getId())
                .ratedUserId(rating.getRatedUser().getId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
