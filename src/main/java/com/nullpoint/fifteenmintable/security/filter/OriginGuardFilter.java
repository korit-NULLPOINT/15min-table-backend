package com.nullpoint.fifteenmintable.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OriginGuardFilter extends OncePerRequestFilter {

    /**
     * 허용할 Origin 목록 (로컬/운영)
     * - 로컬(프론트): http://localhost:5173
     * - 운영: https://your-domain.com
     */
    private final List<String> allowedOrigins;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 쿠키 기반 핵심 엔드포인트만 보호 (Refresh/Logout)
    private static final List<String> PROTECTED_PATHS = List.of(
            "/user/auth/refresh",
            "/user/auth/logout"
    );

    public OriginGuardFilter(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // POST + 지정 경로만 검사
        if (!"POST".equalsIgnoreCase(request.getMethod())) return true;

        String path = request.getRequestURI();
        return PROTECTED_PATHS.stream().noneMatch(p -> pathMatcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        boolean allowed = false;

        // 1) Origin이 있으면 Origin 화이트리스트로 검증
        if (origin != null && !origin.isBlank()) {
            allowed = allowedOrigins.stream().anyMatch(origin::equals);
        }
        // 2) Origin이 없으면 Referer로 보완 검증 (브라우저 케이스 대응)
        else if (referer != null && !referer.isBlank()) {
            allowed = allowedOrigins.stream().anyMatch(referer::startsWith);
        }

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Invalid origin",
                    "path", request.getRequestURI()
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
