package com.nullpoint.fifteenmintable.security.handler;

import com.nullpoint.fifteenmintable.entity.OAuth2User;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.OAuth2UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.auth.AuthTokenService;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthTokenService authTokenService; // ✅ 추가: RT/SSE 쿠키 발급 + 세션 생성 로직 재사용

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("providerUserId");
        String email = defaultOAuth2User.getAttribute("email");

        Optional<OAuth2User> foundOAuth2User =
                oAuth2UserRepository.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);

        // 아직 우리 서비스에 연결(회원가입/연동) 안 된 소셜 계정이면 프론트로 넘김
        if (foundOAuth2User.isEmpty()) {
            response.sendRedirect(frontendUrl + "/auth/oauth2?provider=" + provider
                    + "&providerUserId=" + providerUserId
                    + "&email=" + email);
            return;
        }

        Optional<User> foundUser = userRepository.getUserByUserId(foundOAuth2User.get().getUserId());
        if (foundUser.isEmpty()) {
            throw new RuntimeException("서버에 문제가 발생했습니다.");
        }

        // ✅ 여기서 AT를 '생성'은 하되, URL로 넘기지 않음
        String accessToken = jwtUtils.generateAccessToken(foundUser.get().getUserId().toString());

        // ✅ 핵심: 일반 로그인과 동일하게 RT(+SSE_AT) 쿠키/세션을 심는다
        // (AuthTokenService 내부에서 RT 쿠키 Path=/, HttpOnly/Secure/SameSite, auth_session 생성까지 처리)
        authTokenService.onSigninSuccess(accessToken, request, response);

        // ✅ 토큰 파라미터 없이 프론트 라우트로 이동
        // 프론트(/auth/oauth2/signin)에서 즉시 POST /api/user/auth/refresh 호출 → AT 발급/저장
        response.sendRedirect(frontendUrl + "/auth/oauth2/signin");
    }
}
