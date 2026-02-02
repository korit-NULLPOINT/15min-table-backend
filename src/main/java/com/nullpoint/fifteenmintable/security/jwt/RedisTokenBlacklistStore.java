package com.nullpoint.fifteenmintable.security.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Component
public class RedisTokenBlacklistStore implements TokenBlacklistStore {

    private static final String PREFIX = "bl:jwt:";
    private final StringRedisTemplate redis;

    public RedisTokenBlacklistStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void blacklist(String token, long ttlSeconds) {
        if (token == null || token.isBlank()) return;
        if (ttlSeconds <= 0) return;

        String key = PREFIX + sha256(token);
        redis.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) return false;
        String key = PREFIX + sha256(token);
        return Boolean.TRUE.equals(redis.hasKey(key));
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("sha256 failed", e);
        }
    }
}
