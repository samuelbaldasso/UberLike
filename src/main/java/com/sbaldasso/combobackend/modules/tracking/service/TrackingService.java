package com.sbaldasso.combobackend.modules.tracking.service;

import com.sbaldasso.combobackend.modules.tracking.dto.LocationUpdateDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class TrackingService {
    private final RedisTemplate<String, LocationUpdateDTO> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String LOCATION_KEY_PREFIX = "driver:location:";
    private static final Duration LOCATION_TTL = Duration.ofMinutes(5);

    public TrackingService(RedisTemplate<String, LocationUpdateDTO> redisTemplate,
                          SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void updateDriverLocation(LocationUpdateDTO location) {
        String key = LOCATION_KEY_PREFIX + location.getDriverId();
        redisTemplate.opsForValue().set(key, location, LOCATION_TTL);
        
        // Broadcast to websocket subscribers
        messagingTemplate.convertAndSend(
            "/topic/delivery/" + location.getDeliveryId(),
            location
        );
    }

    public Optional<LocationUpdateDTO> getDriverLocation(Long driverId) {
        String key = LOCATION_KEY_PREFIX + driverId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void removeDriverLocation(Long driverId) {
        String key = LOCATION_KEY_PREFIX + driverId;
        redisTemplate.delete(key);
    }
}
