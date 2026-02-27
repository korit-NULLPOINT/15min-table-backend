package com.nullpoint.fifteenmintable.ratelimit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 기반 Rate Limit(쿨다운) 유틸 서비스
 *
 * - tryAcquireCooldown: SET key value NX EX seconds
 *   -> true  : 이번 요청 허용(키 생성 성공)
 *   -> false : 쿨다운 중(이미 키 존재)
 *
 * - getTtlSeconds: 남은 TTL(초) 조회 (메시지/Retry-After용)
 */
@Service
public class RateLimitRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 쿨다운 획득 시도 (ms)
     *
     * @param key    redis key
     * @param millis cooldown millis (>0)
     */
    public boolean tryAcquireCooldownMs(String key, long millis) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("rate limit key is blank");
        if (millis <= 0) throw new IllegalArgumentException("rate limit millis must be > 0");

        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofMillis(millis));

        return Boolean.TRUE.equals(ok);
    }

    /**
     * 남은 TTL(ms) 조회
     */
    public long getTtlMillis(String key) {
        if (key == null || key.isBlank()) return -2L;

        // getExpire(key, unit) 오버로드 사용 (ms로 받기)
        Long ttl = stringRedisTemplate.getExpire(key, java.util.concurrent.TimeUnit.MILLISECONDS);
        return ttl == null ? -2L : ttl;
    }

    public void deleteKey(String key) {
        if (key == null || key.isBlank()) return;
        stringRedisTemplate.delete(key);
    }
}
