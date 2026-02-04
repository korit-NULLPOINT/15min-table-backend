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
        try {
            redis.opsForValue().set(PREFIX + sessionId, value, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            // fail-open: Redis 장애면 캐시 저장 스킵
        }
    }

    public String get(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return null;
        try {
            return redis.opsForValue().get(PREFIX + sessionId);
        } catch (Exception e) {
            // fail-open: Redis 장애면 캐시 미스 처리
            return null;
        }
    }

    public void delete(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;
        try {
            redis.delete(PREFIX + sessionId);
        } catch (Exception e) {
            // fail-open: Redis 장애면 삭제 스킵
        }
    }
}
