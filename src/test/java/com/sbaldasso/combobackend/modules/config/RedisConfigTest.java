package com.sbaldasso.combobackend.modules.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class RedisConfigTest {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void redisTemplate_shouldBeConfiguredCorrectly() {
        // Assert
        assertNotNull(redisTemplate);
        assertNotNull(redisTemplate.getConnectionFactory());
        assertTrue(redisTemplate.getConnectionFactory().getConnection().ping() != null);
    }

    @Test
    void redisConnection_shouldBeEstablished() {
        // Assert
        assertNotNull(connectionFactory);
        assertTrue(connectionFactory.getConnection().ping() != null);
    }

    @Test
    void redisOperations_shouldWorkCorrectly() {
        // Arrange
        String key = "test:key";
        String value = "test value";

        // Act
        redisTemplate.opsForValue().set(key, value);
        Object retrievedValue = redisTemplate.opsForValue().get(key);

        // Assert
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue);
    }

    @Test
    void redisHash_shouldWorkCorrectly() {
        // Arrange
        String hashKey = "test:hash";
        String field = "field1";
        String value = "hash value";

        // Act
        redisTemplate.opsForHash().put(hashKey, field, value);
        Object retrievedValue = redisTemplate.opsForHash().get(hashKey, field);

        // Assert
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue);
    }

    @Test
    void redisList_shouldWorkCorrectly() {
        // Arrange
        String listKey = "test:list";
        String value = "list item";

        // Act
        redisTemplate.opsForList().rightPush(listKey, value);
        Object retrievedValue = redisTemplate.opsForList().leftPop(listKey);

        // Assert
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue);
    }
}
