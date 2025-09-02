package com.sbaldasso.combobackend.modules.delivery.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.dto.CreateDeliveryRequest;
import com.sbaldasso.combobackend.modules.delivery.dto.DeliveryResponse;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.location.service.LocationService;
import com.sbaldasso.combobackend.modules.notification.service.NotificationService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {
  @Mock
  private DeliveryRepository deliveryRepository;
  @Mock
  private UserService userService;
  @Mock
  private LocationService locationService;
  @Mock
  private NotificationService notificationService;
  @InjectMocks
  private DeliveryService deliveryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createDelivery_savesAndReturnsResponse() {
    UUID customerId = UUID.randomUUID();
    CreateDeliveryRequest request = new CreateDeliveryRequest();
    User customer = new User();
    customer.setId(customerId);
    when(userService.validateAndGetUser(customerId, UserType.CUSTOMER)).thenReturn(customer);
    Delivery delivery = new Delivery();
    delivery.setCustomer(customer);
    when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
    DeliveryResponse response = deliveryService.createDelivery(customerId, request);
    assertNotNull(response);
    verify(deliveryRepository).save(any(Delivery.class));
  }

  @Test
  void getDeliveriesForUser_returnsPage() {
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setUserType(UserType.CUSTOMER);
    when(userService.validateAndGetUser(userId)).thenReturn(user);
    Page<Delivery> deliveries = new PageImpl<>(Collections.emptyList());
    when(deliveryRepository.findByCustomerId(userId, Pageable.unpaged())).thenReturn(deliveries);
    Page<DeliveryResponse> result = deliveryService.getDeliveriesForUser(userId, Pageable.unpaged());
    assertNotNull(result);
  }
}
