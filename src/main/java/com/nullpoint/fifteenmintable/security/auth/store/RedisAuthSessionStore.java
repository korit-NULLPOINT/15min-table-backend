package com.nullpoint.fifteenmintable.security.auth.store;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisAuthSessionStore {

    private static final String PREFIX = "rt:sess:";

    private final StringRedisTemplate redis;

    public RedisAuthSessionStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void save(String sessionId, String value, long ttlSeconds) {
        if (sessionId == null || sessionId.isBlank()) return;
        if (ttlSeconds <= 0) return;
        redis.opsForValue().set(PREFIX + sessionId, value, Duration.ofSeconds(ttlSeconds));
    }

    public String get(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return null;
        return redis.opsForValue().get(PREFIX + sessionId);
    }

    public void delete(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;
        redis.delete(PREFIX + sessionId);
    }

    public boolean exists(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return false;
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + sessionId));
    }
}
