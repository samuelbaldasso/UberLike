package com.sbaldasso.combobackend.modules.rating.repository;

import com.sbaldasso.combobackend.modules.rating.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    
    Page<Rating> findByRatedUserId(UUID userId, Pageable pageable);
    
    Optional<Rating> findByDeliveryIdAndReviewerId(UUID deliveryId, UUID reviewerId);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.ratedUser.id = :userId")
    Double getAverageRatingForUser(UUID userId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser.id = :userId")
    Long countRatingsForUser(UUID userId);
}
