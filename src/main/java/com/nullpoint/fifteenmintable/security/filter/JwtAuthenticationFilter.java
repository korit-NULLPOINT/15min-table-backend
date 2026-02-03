package com.nullpoint.fifteenmintable.security.filter;

import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import com.nullpoint.fifteenmintable.security.auth.store.TokenBlacklistStore;
import com.nullpoint.fifteenmintable.service.PrincipalLoaderService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PrincipalLoaderService principalLoaderService;

    @Autowired
    private TokenBlacklistStore tokenBlacklistStore;

    // ✅ 추가: 컨트롤러/필터 공용 토큰 추출 로직
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        // 1) 헤더 우선
        String accessToken = null;
        if (jwtUtils.isBearer(authorization)) {
            accessToken = jwtUtils.removeBearer(authorization);
        }

        // 2) 헤더 없으면 SSE_AT 쿠키 fallback
        if (accessToken == null || accessToken.trim().isEmpty()) {
            accessToken = getCookieValue(request, SseCookieUtils.SSE_COOKIE_NAME);
        }

        return (accessToken == null || accessToken.trim().isEmpty()) ? null : accessToken;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        List<String> methods = List.of("POST", "GET", "PUT", "PATCH", "DELETE");
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // ✅ 변경: 여기서 공용 메서드 사용
        String accessToken = resolveAccessToken(request);

        if (accessToken != null) {

            // ✅ 블랙리스트면 인증 무효
            if (tokenBlacklistStore.isBlacklisted(accessToken)) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            try {
                Claims claims = jwtUtils.getClaims(accessToken);
                Integer userId = Integer.parseInt(claims.getId());

                principalLoaderService.loadByUserId(userId).ifPresentOrElse(principalUser -> {

                    String status = principalUser.getStatus();
                    if (!"ACTIVE".equals(status)) {
                        SecurityContextHolder.clearContext();
                        return;
                    }

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principalUser, null, principalUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }, SecurityContextHolder::clearContext);

            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

