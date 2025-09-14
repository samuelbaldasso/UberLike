package com.sbaldasso.combobackend.modules.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class CacheServiceTest {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379);

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void setValue_shouldStoreValueInCache() {
        // Arrange
        String key = "test:key";
        String value = "test value";
        Duration ttl = Duration.ofMinutes(5);

        // Act
        cacheService.setValue(key, value, ttl);
        Object cachedValue = redisTemplate.opsForValue().get(key);
        Long expirationTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        // Assert
        assertNotNull(cachedValue);
        assertEquals(value, cachedValue);
        assertTrue(expirationTime > 0 && expirationTime <= ttl.getSeconds());
    }

    @Test
    void getValue_shouldRetrieveValueFromCache() {
        // Arrange
        String key = "test:retrieve";
        String value = "cached value";
        redisTemplate.opsForValue().set(key, value);

        // Act
        Optional<Object> retrievedValue = cacheService.getValue(key);

        // Assert
        assertTrue(retrievedValue.isPresent());
        assertEquals(value, retrievedValue.get());
    }

    @Test
    void getValue_shouldReturnEmptyWhenKeyNotExists() {
        // Arrange
        String nonExistentKey = "test:nonexistent";

        // Act
        Optional<Object> retrievedValue = cacheService.getValue(nonExistentKey);

        // Assert
        assertTrue(retrievedValue.isEmpty());
    }

    @Test
    void deleteValue_shouldRemoveValueFromCache() {
        // Arrange
        String key = "test:delete";
        String value = "value to delete";
        redisTemplate.opsForValue().set(key, value);

        // Act
        cacheService.deleteValue(key);
        Boolean keyExists = redisTemplate.hasKey(key);

        // Assert
        assertFalse(keyExists);
    }

    @Test
    void hasKey_shouldReturnTrueWhenKeyExists() {
        // Arrange
        String key = "test:exists";
        redisTemplate.opsForValue().set(key, "some value");

        // Act
        boolean exists = cacheService.hasKey(key);

        // Assert
        assertTrue(exists);
    }

    @Test
    void hasKey_shouldReturnFalseWhenKeyNotExists() {
        // Arrange
        String nonExistentKey = "test:notexists";

        // Act
        boolean exists = cacheService.hasKey(nonExistentKey);

        // Assert
        assertFalse(exists);
    }

    @Test
    void setValue_shouldUpdateExistingValue() {
        // Arrange
        String key = "test:update";
        String initialValue = "initial value";
        String updatedValue = "updated value";
        Duration ttl = Duration.ofMinutes(5);

        // Act
        cacheService.setValue(key, initialValue, ttl);
        cacheService.setValue(key, updatedValue, ttl);
        Object cachedValue = redisTemplate.opsForValue().get(key);

        // Assert
        assertNotNull(cachedValue);
        assertEquals(updatedValue, cachedValue);
    }

    @Test
    void setValue_shouldHandleNullValue() {
        // Arrange
        String key = "test:null";
        Duration ttl = Duration.ofMinutes(5);

        // Act & Assert
        assertDoesNotThrow(() -> cacheService.setValue(key, null, ttl));
        assertNull(redisTemplate.opsForValue().get(key));
    }
}
