package com.nullpoint.fifteenmintable.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class SseCookieUtils {

    public static final String SSE_COOKIE_NAME = "SSE_AT";

    /**
     * SSE 인증용 AccessToken 쿠키 세팅
     * - HttpOnly라 프론트 JS로 못 읽지만, EventSource 요청에 자동으로 포함됨
     */
    public void setSseAccessToken(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from(SSE_COOKIE_NAME, accessToken)
                .httpOnly(true)
                .secure(false)          // 로컬: false, https 배포: true
                .sameSite("Lax")        // 프록시로 같은 오리진이면 Lax로 충분
                .path("/notifications") // ✅ SSE 엔드포인트 prefix에 맞춰 수정
                .maxAge(60L * 60L * 24L * 30L)        // 토큰 만료에 맞추는 걸 추천
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** SSE 인증용 쿠키 제거 */
    public void clearSseAccessToken(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SSE_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/notifications") // set할 때와 동일해야 함
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
