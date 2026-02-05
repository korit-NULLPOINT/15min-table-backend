package com.nullpoint.fifteenmintable.config;

import com.nullpoint.fifteenmintable.security.filter.JwtAuthenticationFilter;
import com.nullpoint.fifteenmintable.security.filter.OriginGuardFilter;
import com.nullpoint.fifteenmintable.security.handler.OAuth2SuccessHandler;
import com.nullpoint.fifteenmintable.security.handler.RestAccessDeniedHandler;
import com.nullpoint.fifteenmintable.security.handler.RestAuthenticationEntryPoint;
import com.nullpoint.fifteenmintable.service.OAuth2PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2PrincipalService oAuth2PrincipalService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 최소 권장(나중에 properties로 빼기)
        corsConfiguration.addAllowedOriginPattern("http://localhost:*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public OriginGuardFilter originGuardFilter() {
        return new OriginGuardFilter(List.of(
                "http://localhost:5173", // 프론트
                "http://localhost:8080"  // Swagger
        ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.formLogin(formLogin -> formLogin.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.logout(logout -> logout.disable());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(originGuardFilter(), SecurityContextHolderFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(eh -> eh
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
        );

        http.authorizeHttpRequests(auth -> {

            // ✅ Preflight
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

            // ✅ 관리자
            auth.requestMatchers("/admin/**").hasRole("ADMIN");

            // ✅ 공개(Auth 전체 + Swagger + OAuth2 + verify)
            auth.requestMatchers(
                    "/user/auth/**",
                    "/mail/verify",
                    "/login/oauth2/**",
                    "/oauth2/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
            ).permitAll();

            // ✅ 읽기(GET) 공개
            auth.requestMatchers(HttpMethod.GET,
                    "/board/*/recipes/list",
                    "/board/*/recipes/detail/*",
                    "/board/*/free/list",
                    "/board/*/free/detail",
                    "/comment/list/*",
                    "/users/profile/*",
                    "/recipes/user/*",
                    "/posts/user/*",
                    "/recipe-hashtag/search",
                    "/recipe-hashtag/list/*",
                    "/rating/*/summary"
            ).permitAll();

            // ✅ TEMP 포함 보호영역 (메일 인증 플로우)
            // /mail/send: TEMP_USER -> USER 전환 과정에서 필요하니 TEMP_USER 허용이 맞음
            auth.requestMatchers(
                    "/mail/send",
                    "/user/account/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");

            // ✅ 쓰기(POST/PUT/DELETE) - USER/ADMIN
            auth.requestMatchers(HttpMethod.POST,
                    "/board/**",
                    "/comment/**",
                    "/bookmark/**",
                    "/rating/**",
                    "/recipe-hashtag/**",
                    "/notifications/**",
                    "/ai/**"
            ).hasAnyRole("ADMIN", "USER");

            auth.requestMatchers(HttpMethod.PUT, "/board/**")
                    .hasAnyRole("ADMIN", "USER");

            auth.requestMatchers(HttpMethod.DELETE, "/board/**")
                    .hasAnyRole("ADMIN", "USER");

            // ✅ 기타 보호
            auth.requestMatchers(
                    "/follow/**",
                    "/notifications/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");

            // 나머지
            auth.anyRequest().authenticated();
        });

        http.oauth2Login(oauth2 ->
                oauth2.userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2PrincipalService))
                        .successHandler(oAuth2SuccessHandler)
        );

        return http.build();
    }
}
