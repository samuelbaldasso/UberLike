package com.sbaldasso.combobackend.modules.rating.controller;

import com.sbaldasso.combobackend.modules.rating.dto.CreateRatingRequest;
import com.sbaldasso.combobackend.modules.rating.dto.RatingResponse;
import com.sbaldasso.combobackend.modules.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/deliveries/{deliveryId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER')")
    public ResponseEntity<RatingResponse> createRating(
            @PathVariable UUID deliveryId,
            @RequestAttribute UUID userId,
            @Valid @RequestBody CreateRatingRequest request) {
        return ResponseEntity.ok(ratingService.createRating(deliveryId, userId, request));
    }

    @GetMapping("/{ratingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<RatingResponse> getRating(@PathVariable UUID ratingId) {
        return ResponseEntity.ok(ratingService.getRating(ratingId));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<Page<RatingResponse>> getUserRatings(
            @PathVariable UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId, pageable));
    }

    @GetMapping("/users/{userId}/average")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<Double> getUserAverageRating(@PathVariable UUID userId) {
        return ResponseEntity.ok(ratingService.getUserAverageRating(userId));
    }

    @GetMapping("/users/{userId}/count")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<Long> getUserRatingCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(ratingService.getUserRatingCount(userId));
    }
}
