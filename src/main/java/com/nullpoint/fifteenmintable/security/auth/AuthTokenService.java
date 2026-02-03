package com.nullpoint.fifteenmintable.security.auth;

import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.security.auth.session.AuthSessionService;
import com.nullpoint.fifteenmintable.security.auth.store.TokenBlacklistStore;
import com.nullpoint.fifteenmintable.security.auth.token.RefreshTokenService;
import com.nullpoint.fifteenmintable.security.cookie.RefreshCookieUtils;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
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
    public String refresh(String refreshToken, HttpServletResponse response) {
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

        return newAccess;
    }

    /**
     * logout:
     * - refresh 세션 revoke + refresh 쿠키 제거 + SSE 쿠키 제거
     * - access 블랙리스트 등록(즉시 무효화)
     *
     * 주의:
     * - accessToken은 Bearer 포함으로 들어올 수 있으니 정리 후 처리
     * - refreshToken이 없더라도 쿠키 삭제/블랙리스트는 가능한 범위에서 진행
     */
    public void logout(String refreshToken, String accessToken, HttpServletResponse response) {

        // 1) refresh 세션 revoke (refreshToken이 있을 때만)
        if (!isBlank(refreshToken)) {
            String sessionId = refreshTokenService.extractSessionId(refreshToken);
            if (!isBlank(sessionId)) {
                authSessionService.revokeSession(sessionId);
            }
        }

        // 2) 쿠키 제거 (항상 시도)
        refreshCookieUtils.clearRefreshToken(response, cookieSecure);
        sseCookieUtils.clearSseAccessToken(response);

        // 3) access 블랙리스트 (UserAuthService.logout 로직과 동일한 방식)
        if (isBlank(accessToken)) return;

        // Bearer 제거
        if (jwtUtils.isBearer(accessToken)) {
            accessToken = jwtUtils.removeBearer(accessToken);
            if (isBlank(accessToken)) return;
        }

        try {
            Claims claims = jwtUtils.getClaims(accessToken);

            Date exp = claims.getExpiration();
            if (exp == null) return;

            long ttlSeconds = (exp.getTime() - System.currentTimeMillis()) / 1000L;
            if (ttlSeconds > 0) {
                tokenBlacklistStore.blacklist(accessToken, ttlSeconds);
            }
        } catch (RuntimeException ignore) {
            // 만료/위조/파싱 실패면 블랙리스트 등록 스킵
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
