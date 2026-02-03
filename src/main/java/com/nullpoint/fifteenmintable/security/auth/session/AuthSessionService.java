package com.nullpoint.fifteenmintable.security.auth.session;

import com.nullpoint.fifteenmintable.entity.AuthSession;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.mapper.AuthSessionMapper;

import com.nullpoint.fifteenmintable.security.auth.store.RedisAuthSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthSessionService {

    @Autowired
    private AuthSessionMapper authSessionMapper;

    @Autowired
    private RedisAuthSessionStore redisAuthSessionStore;

    /**
     * Redis value 포맷 (단순 문자열)
     * userId|refreshHash|expiresEpochSec|status
     */
    private String encodeCache(Integer userId, String refreshHash, LocalDateTime expiresDt, String status) {
        long exp = expiresDt.toEpochSecond(ZoneOffset.UTC);
        return userId + "|" + refreshHash + "|" + exp + "|" + status;
    }

    private CachedSession decodeCache(String value) {
        try {
            String[] parts = value.split("\\|", -1);
            if (parts.length < 4) return null;
            Integer userId = Integer.parseInt(parts[0]);
            String refreshHash = parts[1];
            long exp = Long.parseLong(parts[2]);
            String status = parts[3];
            LocalDateTime expiresDt = LocalDateTime.ofEpochSecond(exp, 0, ZoneOffset.UTC);
            return new CachedSession(userId, refreshHash, expiresDt, status);
        } catch (Exception e) {
            return null;
        }
    }

    private long ttlSeconds(LocalDateTime expiresDt) {
        long ttl = Duration.between(LocalDateTime.now(), expiresDt).getSeconds();
        return Math.max(0, ttl);
    }

    /** 로그인 성공 시 세션 생성(원장:DB + 캐시:Redis) */
    public String createSession(Integer userId, String refreshHash, LocalDateTime expiresDt, String ip, String userAgent) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        AuthSession session = new AuthSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setRefreshTokenHash(refreshHash);
        session.setStatus("ACTIVE");
        session.setIp(ip);
        session.setUserAgent(userAgent);
        session.setLastUsedDt(now);
        session.setExpiresDt(expiresDt);
        session.setRevokedDt(null);
        session.setCreateDt(now);
        session.setUpdateDt(now);

        authSessionMapper.insertSession(session);

        // Redis 캐시 저장(TTL=만료까지)
        String cacheValue = encodeCache(userId, refreshHash, expiresDt, "ACTIVE");
        redisAuthSessionStore.save(sessionId, cacheValue, ttlSeconds(expiresDt));

        return sessionId;
    }

    /**
     * refresh 검증용 조회: Redis → (miss) DB → Redis 워밍
     * - ACTIVE + 만료 확인까지 여기서 수행
     */
    public SessionCheck getActiveSessionForRefresh(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new UnauthenticatedException("세션이 유효하지 않습니다.");
        }

        // 1) Redis hit
        String cached = redisAuthSessionStore.get(sessionId);
        if (cached != null) {
            CachedSession cs = decodeCache(cached);
            if (cs != null) {
                if (!"ACTIVE".equals(cs.status)) throw new UnauthenticatedException("세션이 유효하지 않습니다.");
                if (cs.expiresDt.isBefore(LocalDateTime.now())) throw new UnauthenticatedException("세션이 만료되었습니다.");
                return new SessionCheck(cs.userId, cs.refreshHash, cs.expiresDt);
            }
            // 캐시가 깨졌으면 삭제하고 DB로 fallback
            redisAuthSessionStore.delete(sessionId);
        }

        // 2) Redis miss → DB 조회
        Optional<AuthSession> opt = authSessionMapper.getBySessionId(sessionId);
        AuthSession session = opt.orElseThrow(() -> new UnauthenticatedException("세션이 없거나 만료되었습니다."));

        if (!"ACTIVE".equals(session.getStatus())) throw new UnauthenticatedException("세션이 유효하지 않습니다.");
        if (session.getExpiresDt() == null || session.getExpiresDt().isBefore(LocalDateTime.now())) {
            throw new UnauthenticatedException("세션이 만료되었습니다.");
        }

        // 3) Redis 워밍
        String cacheValue = encodeCache(session.getUserId(), session.getRefreshTokenHash(), session.getExpiresDt(), session.getStatus());
        redisAuthSessionStore.save(sessionId, cacheValue, ttlSeconds(session.getExpiresDt()));

        return new SessionCheck(session.getUserId(), session.getRefreshTokenHash(), session.getExpiresDt());
    }

    /** refresh 회전: DB update 후 Redis 갱신(+TTL 연장) */
    public void rotateSession(String sessionId, Integer userId, String newRefreshHash, LocalDateTime newExpiresDt) {
        LocalDateTime now = LocalDateTime.now();

        int updated = authSessionMapper.rotateSession(
                sessionId,
                newRefreshHash,
                newExpiresDt,
                now,
                now
        );

        if (updated == 0) {
            throw new UnauthenticatedException("세션 갱신에 실패했습니다.");
        }

        String cacheValue = encodeCache(userId, newRefreshHash, newExpiresDt, "ACTIVE");
        redisAuthSessionStore.save(sessionId, cacheValue, ttlSeconds(newExpiresDt));
    }

    /** 로그아웃/의심상황: DB revoke 후 Redis 삭제 */
    public void revokeSession(String sessionId) {
        LocalDateTime now = LocalDateTime.now();
        authSessionMapper.revokeSession(sessionId, now, now);
        redisAuthSessionStore.delete(sessionId);
    }

    // ===== inner structs =====

    /** refresh 검증에 필요한 최소 정보 */
    public record SessionCheck(Integer userId, String refreshHash, LocalDateTime expiresDt) {}

    private record CachedSession(Integer userId, String refreshHash, LocalDateTime expiresDt, String status) {}
}
