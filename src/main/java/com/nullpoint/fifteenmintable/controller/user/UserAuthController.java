package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.auth.SigninReqDto;
import com.nullpoint.fifteenmintable.dto.auth.SignupReqDto;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.filter.JwtAuthenticationFilter;
import com.nullpoint.fifteenmintable.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private SseCookieUtils sseCookieUtils;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostMapping("/signup")
    public ResponseEntity<ApiRespDto<Void>> signup(@RequestBody SignupReqDto signupReqDto) {
        return ResponseEntity.ok(userAuthService.signup(signupReqDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiRespDto<String>> signin(
            @RequestBody SigninReqDto signinReqDto,
            HttpServletResponse response
    ) {
        ApiRespDto<String> respDto = userAuthService.signin(signinReqDto);

        // SSE 쿠키 심기
        String accessToken = respDto.getData();
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            sseCookieUtils.setSseAccessToken(response, accessToken);
        }

        return ResponseEntity.ok(respDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRespDto<Void>> logout(HttpServletRequest request, HttpServletResponse response) {

        // SSE 쿠키 삭제
        sseCookieUtils.clearSseAccessToken(response);

        // 필터의 토큰 추출 로직 재사용 (헤더→쿠키)
        String accessToken = jwtAuthenticationFilter.resolveAccessToken(request);

        // 블랙리스트 등록
        return ResponseEntity.ok(userAuthService.logout(accessToken));
    }
}
