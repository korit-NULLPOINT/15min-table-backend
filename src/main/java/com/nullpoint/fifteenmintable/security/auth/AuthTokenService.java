package com.nullpoint.fifteenmintable.security.auth;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.security.auth.session.AuthSessionService;
import com.nullpoint.fifteenmintable.security.auth.store.TokenBlacklistStore;
import com.nullpoint.fifteenmintable.security.auth.token.RefreshTokenService;
import com.nullpoint.fifteenmintable.security.cookie.RefreshCookieUtils;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthTokenService {

    @Autowired
    private AuthSessionService authSessionService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshCookieUtils refreshCookieUtils;

    @Autowired
    private SseCookieUtils sseCookieUtils;

    @Autowired
    private TokenBlacklistStore tokenBlacklistStore; // RedisTokenBlacklistStore 주입됨

    @Autowired
    private JwtUtils jwtUtils;

    // 로컬이면 false, 배포 HTTPS면 true (나중에 설정값으로 빼면 됨)
    private final boolean cookieSecure = false;

    // refresh 만료 정책 (예: 30일)
    private final long refreshMaxAgeSeconds = 60L * 60L * 24L * 30L;

    /**
     * refresh:
     * - refresh 쿠키 값(opaque)을 받아서 검증/회전하고
     * - 새 access를 반환 + 쿠키 갱신(refresh + sse)
     */
    public ApiRespDto<String> refresh(String refreshToken, HttpServletResponse response) {
        if (isBlank(refreshToken)) {
            throw new UnauthenticatedException("리프레시 토큰이 유효하지 않습니다.");
        }

        String sessionId = refreshTokenService.extractSessionId(refreshToken);
        String secret = refreshTokenService.extractSecret(refreshToken);
        if (sessionId == null || secret == null) {
            throw new UnauthenticatedException("리프레시 토큰이 유효하지 않습니다.");
        }

        // 1) 세션 조회(REDIS→DB)
        AuthSessionService.SessionCheck session = authSessionService.getActiveSessionForRefresh(sessionId);

        // 2) secret hash 비교
        String incomingHash = refreshTokenService.hashSecret(secret);
        if (!incomingHash.equals(session.refreshHash())) {
            // 탈취/재사용 의심: 즉시 revoke 권장
            authSessionService.revokeSession(sessionId);
            throw new UnauthenticatedException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 3) 회전(rotate)
        String newRefreshToken = refreshTokenService.issueRefreshToken(sessionId);
        String newSecret = refreshTokenService.extractSecret(newRefreshToken);
        String newHash = refreshTokenService.hashSecret(newSecret);

        LocalDateTime newExpires = LocalDateTime.now().plusSeconds(refreshMaxAgeSeconds);
        authSessionService.rotateSession(sessionId, session.userId(), newHash, newExpires);

        // RT 쿠키 갱신
        refreshCookieUtils.setRefreshToken(response, newRefreshToken, cookieSecure);

        // 4) 새 access 발급 + SSE 쿠키 갱신
        String newAccess = jwtUtils.generateAccessToken(String.valueOf(session.userId()));
        sseCookieUtils.setSseAccessToken(response, newAccess);

        return new ApiRespDto<>("success", "토큰이 재발급되었습니다.", newAccess);
    }

    public void onSigninSuccess(
            String accessToken, HttpServletRequest request, HttpServletResponse response
    ) {
        if (isBlank(accessToken)) return;

        // 1) SSE 쿠키
        sseCookieUtils.setSseAccessToken(response, accessToken);

        // 2) userId 추출
        Integer userId;
        try {
            userId = Integer.parseInt(jwtUtils.getClaims(accessToken).getId());
        } catch (RuntimeException e) {
            return;
        }

        // 3) expires
        LocalDateTime expiresDt = LocalDateTime.now().plusSeconds(refreshMaxAgeSeconds);

        // 4) sessionId를 먼저 만든다 (refreshToken이 sessionId를 포함하니까)
        String sessionId = java.util.UUID.randomUUID().toString();

        // 5) refreshToken 발급(여기서 secret이 생성됨)
        String refreshToken = refreshTokenService.issueRefreshToken(sessionId);

        // 6) secret 추출 -> hash 만들기
        String secret = refreshTokenService.extractSecret(refreshToken);
        String refreshHash = refreshTokenService.hashSecret(secret);

        // 7) ip/ua
        String ip = getClientIpSimple(request);
        String userAgent = request.getHeader("User-Agent");

        // 8) DB+Redis 세션 저장 (sessionId 고정)
        authSessionService.createSessionWithId(sessionId, userId, refreshHash, expiresDt, ip, userAgent);

        // 9) RT 쿠키 심기
        refreshCookieUtils.setRefreshToken(response, refreshToken, cookieSecure);
    }

    private String getClientIpSimple(jakarta.servlet.http.HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int idx = xff.indexOf(",");
            return (idx > 0) ? xff.substring(0, idx).trim() : xff.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * logout (멱등/상시 200 OK):
     * - refreshToken 있으면: sessionId 추출 → revoke(best-effort)
     * - accessToken 있으면: exp 기반 ttl 계산 → 블랙리스트(best-effort)
     * - 마지막에는 항상 쿠키 삭제(RT, SSE_AT)
     * 중요:
     * - 인증이 없어도(AT/RT 둘 다 없어도) 무조건 success 반환
     * - Redis/DB 예외가 나도 로그아웃 실패시키지 않음 (특히 Redis down 상황)
     */
    public ApiRespDto<Void> logout(String refreshToken, String accessToken, HttpServletResponse response) {

        // 0) 먼저 토큰 문자열 정리 (Bearer 제거 등)
        String normalizedAccess = normalizeAccessToken(accessToken);

        // 1) refresh 세션 revoke (best-effort)
        try {
            if (!isBlank(refreshToken)) {
                String sessionId = refreshTokenService.extractSessionId(refreshToken);
                if (!isBlank(sessionId)) {
                    authSessionService.revokeSession(sessionId);
                }
            }
        } catch (Exception ignore) {
            // best-effort: revoke 실패해도 로그아웃은 성공 처리
            // 필요하면 log.warn(...)만 추가
        }

        // 2) access 블랙리스트 등록 (best-effort)
        try {
            if (!isBlank(normalizedAccess)) {
                Date exp = null;
                try {
                    Claims claims = jwtUtils.getClaims(normalizedAccess);
                    exp = claims.getExpiration();
                } catch (RuntimeException ignore) {
                    // 파싱 불가면 블랙리스트 스킵
                    exp = null;
                }

                if (exp != null) {
                    long ttlSeconds = (exp.getTime() - System.currentTimeMillis()) / 1000L;
                    if (ttlSeconds > 0) {
                        tokenBlacklistStore.blacklist(normalizedAccess, ttlSeconds);
                    }
                }
            }
        } catch (Exception ignore) {
            // best-effort: Redis down/timeout 등이어도 로그아웃은 성공 처리
            // 필요하면 log.warn(...)만 추가
        }

        // 3) 쿠키 제거는 항상 수행 (절대 실패시키지 않기)
        try {
            refreshCookieUtils.clearRefreshToken(response, cookieSecure);
        } catch (Exception ignore) {
        }
        try {
            sseCookieUtils.clearSseAccessToken(response);
        } catch (Exception ignore) {
        }

        // 4) 항상 200 OK
        return new ApiRespDto<>("success", "로그아웃 되었습니다.", null);
    }

    /**
     * Authorization 헤더 값이 들어오든, 이미 토큰만 들어오든, 최대한 정상화해서 반환.
     */
    private String normalizeAccessToken(String accessToken) {
        if (isBlank(accessToken)) return null;

        String t = accessToken.trim();

        // "Bearer xxx" 형태면 제거
        try {
            if (jwtUtils.isBearer(t)) {
                t = jwtUtils.removeBearer(t);
            }
        } catch (Exception ignore) {
            // jwtUtils 내부에서 예외 나면 그냥 아래 fallback 로직으로 처리
            if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
                t = t.substring(7).trim();
            }
        }

        return isBlank(t) ? null : t;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
