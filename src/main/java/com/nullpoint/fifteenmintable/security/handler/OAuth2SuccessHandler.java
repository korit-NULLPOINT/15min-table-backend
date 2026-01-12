package com.nullpoint.fifteenmintable.security.handler;
import com.nullpoint.fifteenmintable.entity.OAuth2User;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.OAuth2UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("providerUserId");
        String email = defaultOAuth2User.getAttribute("email");

        Optional<OAuth2User> foundOAuth2User = oAuth2UserRepository.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);

        if (foundOAuth2User.isEmpty()) {
            response.sendRedirect("http://localhost:5173/auth/oauth2?provider="+provider+"&providerUserId="+providerUserId+"&email="+email);
            return;
        }

        Optional<User> foundUser = userRepository.getUserByUserId(foundOAuth2User.get().getUserId());
        if (foundUser.isEmpty()) {
            throw new RuntimeException("서버에 문제가 발생했습니다.");
        }

        String accessToken = jwtUtils.generateAccessToken(foundUser.get().getUserId().toString());

        response.sendRedirect("http://localhost:5173/auth/oauth2/signin?accessToken="+accessToken);
    }
}
