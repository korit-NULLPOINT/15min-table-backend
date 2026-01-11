package com.nullpoint.fifteenmintable.security.filter;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();

        // 🔥 auth / oauth 경로는 JWT 검사 자체 스킵
        if (path.startsWith("/user/auth/")
                || path.startsWith("/admin/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.equals("/mail/verify")) {

            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (jwtUtils.isBearer(authorization)) {
            try {
                String accessToken = jwtUtils.removeBearer(authorization);
                Claims claims = jwtUtils.getClaims(accessToken);

                Integer userId = Integer.parseInt(claims.getId());
                User user = userRepository.getUserByUserId(userId)
                        .orElseThrow();

                PrincipalUser principalUser = PrincipalUser.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .username(user.getUsername())
                        .profileImg(user.getProfileImg())
                        .status(user.getStatus())
                        .userRoles(user.getUserRoles())
                        .build();

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                principalUser, null, principalUser.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // ❗ 여기서 SecurityContext 비우고 그냥 통과
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
