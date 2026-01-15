package com.nullpoint.fifteenmintable.config;
import com.nullpoint.fifteenmintable.security.filter.JwtAuthenticationFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
        corsConfiguration.addAllowedOriginPattern("http://localhost:5173");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.formLogin(formLogin -> formLogin.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.logout(logout -> logout.disable());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(eh -> eh
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
        );


        http.authorizeHttpRequests(auth -> {

            // 0) 공개
            auth.requestMatchers(
                    "/user/auth/**",
                    "/mail/verify",
                    "/login/oauth2/**",
                    "/oauth2/**"
            ).permitAll();

            // 1) 읽기(GET) 공개
            auth.requestMatchers(HttpMethod.GET,
                    "/board/*/recipes/list",
                    "/board/*/recipes/detail/*",
                    "/comment/list/*",
                    "/rating/*/summary"
            ).permitAll();

            // 2) TEMP 전용(메일 인증/상태 확인 등)
            auth.requestMatchers(
                    "/mail/send",
                    "/user/account/principal"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");

            // 3) 쓰기(POST/PUT/DELETE)
            auth.requestMatchers(HttpMethod.POST,
                    "/board/**",
                    "/comment/**",
                    "/bookmark/**",
                    "/rating/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");   // ✅ 나중에 TEMP_USER 빼면 됨

            auth.requestMatchers(HttpMethod.PUT,
                    "/board/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");   // ✅ 나중에 TEMP_USER 빼면 됨

            auth.requestMatchers(HttpMethod.DELETE,
                    "/board/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");   // ✅ 나중에 TEMP_USER 빼면 됨

            // 기타 보호
            auth.requestMatchers(
                    "/follow/**",
                    "/notification/**",
                    "/user/account/**"
            ).hasAnyRole("ADMIN", "USER", "TEMP_USER");

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










