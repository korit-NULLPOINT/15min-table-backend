package com.nullpoint.fifteenmintable.ratelimit.aspect;

import com.nullpoint.fifteenmintable.exception.TooManyRequestsException;
import com.nullpoint.fifteenmintable.ratelimit.annotation.RateLimit;
import com.nullpoint.fifteenmintable.ratelimit.service.RateLimitRedisService;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitRedisService redis;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        String key = buildKey(pjp, rateLimit);

        boolean acquired = redis.tryAcquireCooldown(key, rateLimit.seconds());
        if (!acquired) {
            long ttl = redis.getTtlSeconds(key);
            // ttl이 0~양수면 "n초 후" 안내 가능
            if (ttl > 0) {
                throw new TooManyRequestsException("너무 잦은 요청입니다. " + ttl + "초 후 다시 시도해주세요.");
            }
            throw new TooManyRequestsException("너무 잦은 요청입니다. 잠시 후 다시 시도해주세요.");
        }

        return pjp.proceed();
    }

    private String buildKey(ProceedingJoinPoint pjp, RateLimit rateLimit) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 커스텀 키가 있으면 그걸 우선 사용(고정 문자열)
        String custom = rateLimit.key();
        String suffix = (custom != null && !custom.isBlank()) ? custom : (method + ":" + uri);

        switch (rateLimit.scope()) {
            case IP -> {
                String ip = extractClientIp(request);
                return "rl:ip:" + ip + ":" + suffix;
            }
            case USER -> {
                Integer userId = extractUserId(pjp);
                // userId를 못 얻으면 IP로 fallback (원하면 여기서 401 던지게 바꿔도 됨)
                if (userId == null) {
                    String ip = extractClientIp(request);
                    return "rl:ip:" + ip + ":" + suffix;
                }
                return "rl:user:" + userId + ":" + suffix;
            }
            default -> {
                // 안전장치
                String ip = extractClientIp(request);
                return "rl:ip:" + ip + ":" + suffix;
            }
        }
    }

    /**
     * PrincipalUser를 메서드 인자에서 찾아 userId 추출
     * - 컨트롤러 메서드 시그니처에 PrincipalUser가 들어오는 구조라면 이 방식이 제일 단순함
     */
    private Integer extractUserId(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args == null) return null;

        for (Object arg : args) {
            if (arg instanceof PrincipalUser principalUser) {
                return principalUser.getUserId();
            }
        }
        return null;
    }

    /**
     * 클라이언트 IP 추출 (프록시 환경 고려)
     */
    private String extractClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // 첫 번째 IP가 원본인 경우가 많음
            String first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }

        String xrip = req.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return req.getRemoteAddr();
    }
}
