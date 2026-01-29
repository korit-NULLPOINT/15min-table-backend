package com.nullpoint.fifteenmintable.security.filter;

import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        List<String> methods = List.of("POST", "GET", "PUT", "PATCH", "DELETE");
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // ✅ 1) 헤더 우선
        String accessToken = null;
        if (jwtUtils.isBearer(authorization)) {
            accessToken = jwtUtils.removeBearer(authorization);
        }

        // ✅ 2) 헤더 없으면 SSE_AT 쿠키 fallback (EventSource는 헤더 못 넣음)
        if (accessToken == null || accessToken.trim().isEmpty()) {
            accessToken = getCookieValue(request, SseCookieUtils.SSE_COOKIE_NAME);
        }

        if (accessToken != null && !accessToken.trim().isEmpty()) {
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
