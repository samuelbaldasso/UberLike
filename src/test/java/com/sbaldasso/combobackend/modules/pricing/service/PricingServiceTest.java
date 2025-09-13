package com.sbaldasso.combobackend.modules.pricing.service;

import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryDistance;
import com.sbaldasso.combobackend.modules.pricing.config.PricingConfig;
import com.sbaldasso.combobackend.modules.pricing.domain.PriceCalculation;
import com.sbaldasso.combobackend.modules.pricing.dto.PriceEstimateRequest;
import com.sbaldasso.combobackend.modules.pricing.dto.PriceEstimateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    @Mock
    private PricingConfig pricingConfig;

    @Mock
    private DeliveryDistanceService distanceService;

    @InjectMocks
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupPricingConfig();
    }

    private void setupPricingConfig() {
        when(pricingConfig.getBasePrice()).thenReturn(BigDecimal.valueOf(5.00));
        when(pricingConfig.getPricePerKm()).thenReturn(BigDecimal.valueOf(2.00));
        when(pricingConfig.getPricePerMinute()).thenReturn(BigDecimal.valueOf(0.50));
        when(pricingConfig.getDynamicPricingFactor()).thenReturn(BigDecimal.valueOf(1.5));
        when(pricingConfig.getServiceFeePercentage()).thenReturn(BigDecimal.valueOf(0.15));
    }

    @Test
    void calculatePrice_shouldReturnCorrectBasePrice() {
        // Arrange
        PriceEstimateRequest request = createSampleRequest();
        DeliveryDistance distance = new DeliveryDistance(5.0, 15);
        when(distanceService.calculateDistance(any(), any())).thenReturn(distance);

        // Act
        PriceEstimateResponse response = pricingService.calculatePrice(request);

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(22.50), response.getBasePrice()); // 5 + (5km * 2.00) + (15min * 0.50)
    }

    @Test
    void calculatePrice_withDynamicPricing_shouldApplyMultiplier() {
        // Arrange
        PriceEstimateRequest request = createPeakHourRequest();
        DeliveryDistance distance = new DeliveryDistance(5.0, 15);
        when(distanceService.calculateDistance(any(), any())).thenReturn(distance);
        when(pricingService.isDynamicPricingActive()).thenReturn(true);

        // Act
        PriceEstimateResponse response = pricingService.calculatePrice(request);

        // Assert
        BigDecimal expectedPrice = BigDecimal.valueOf(22.50).multiply(BigDecimal.valueOf(1.5));
        assertEquals(expectedPrice, response.getTotalPrice());
    }

    @Test
    void calculatePrice_shouldApplyServiceFee() {
        // Arrange
        PriceEstimateRequest request = createSampleRequest();
        DeliveryDistance distance = new DeliveryDistance(5.0, 15);
        when(distanceService.calculateDistance(any(), any())).thenReturn(distance);

        // Act
        PriceEstimateResponse response = pricingService.calculatePrice(request);

        // Assert
        BigDecimal serviceFee = response.getBasePrice().multiply(BigDecimal.valueOf(0.15));
        assertEquals(serviceFee, response.getServiceFee());
        assertEquals(response.getBasePrice().add(serviceFee), response.getTotalPrice());
    }

    @Test
    void calculatePrice_withMinimumDistance_shouldUseMinimumPrice() {
        // Arrange
        PriceEstimateRequest request = createSampleRequest();
        DeliveryDistance distance = new DeliveryDistance(0.5, 5);
        when(distanceService.calculateDistance(any(), any())).thenReturn(distance);
        when(pricingConfig.getMinimumPrice()).thenReturn(BigDecimal.valueOf(10.00));

        // Act
        PriceEstimateResponse response = pricingService.calculatePrice(request);

        // Assert
        assertTrue(response.getBasePrice().compareTo(BigDecimal.valueOf(10.00)) >= 0);
    }

    @Test
    void calculatePrice_withLongDistance_shouldApplyDiscount() {
        // Arrange
        PriceEstimateRequest request = createSampleRequest();
        DeliveryDistance distance = new DeliveryDistance(20.0, 45);
        when(distanceService.calculateDistance(any(), any())).thenReturn(distance);
        when(pricingConfig.getLongDistanceDiscountThreshold()).thenReturn(15.0);
        when(pricingConfig.getLongDistanceDiscountPercentage()).thenReturn(BigDecimal.valueOf(0.10));

        // Act
        PriceEstimateResponse response = pricingService.calculatePrice(request);

        // Assert
        assertTrue(response.getDiscount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculatePrice_withInvalidCoordinates_shouldThrowException() {
        // Arrange
        PriceEstimateRequest request = createInvalidRequest();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pricingService.calculatePrice(request));
    }

    private PriceEstimateRequest createSampleRequest() {
        return new PriceEstimateRequest(
            -23.550520, -46.633308, // São Paulo coordinates
            -23.557821, -46.639680, // Destination coordinates
            LocalDateTime.now()
        );
    }

    private PriceEstimateRequest createPeakHourRequest() {
        return new PriceEstimateRequest(
            -23.550520, -46.633308,
            -23.557821, -46.639680,
            LocalDateTime.now().withHour(18) // Peak hour
        );
    }

    private PriceEstimateRequest createInvalidRequest() {
        return new PriceEstimateRequest(
            200.0, 200.0, // Invalid coordinates
            -23.557821, -46.639680,
            LocalDateTime.now()
        );
    }
}
