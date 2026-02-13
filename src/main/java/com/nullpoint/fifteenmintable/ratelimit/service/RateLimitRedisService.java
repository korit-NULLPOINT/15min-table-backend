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
     * 쿨다운 획득 시도
     *
     * @param key     redis key
     * @param seconds cooldown seconds (>0)
     * @return true if acquired, false if already exists
     */
    public boolean tryAcquireCooldown(String key, int seconds) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("rate limit key is blank");
        if (seconds <= 0) throw new IllegalArgumentException("rate limit seconds must be > 0");

        // SET key "1" NX EX seconds
        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(seconds));

        // setIfAbsent may return null in some edge cases -> treat as not acquired
        return Boolean.TRUE.equals(ok);
    }

    /**
     * 남은 TTL(초) 조회
     * - 키가 없으면 -2, 만료가 없으면 -1 같은 값이 나올 수 있음(드라이버/설정에 따라)
     */
    public long getTtlSeconds(String key) {
        if (key == null || key.isBlank()) return -2L;
        Long ttl = stringRedisTemplate.getExpire(key);
        return ttl == null ? -2L : ttl;
    }

    /**
     * 키 삭제 (선택적으로 사용)
     */
    public void deleteKey(String key) {
        if (key == null || key.isBlank()) return;
        stringRedisTemplate.delete(key);
    }
}
