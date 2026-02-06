package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.auth.SigninReqDto;
import com.nullpoint.fifteenmintable.dto.auth.SignupReqDto;
import com.nullpoint.fifteenmintable.security.auth.AuthTokenService;
import com.nullpoint.fifteenmintable.security.cookie.RefreshCookieUtils;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.security.filter.JwtAuthenticationFilter;
import com.nullpoint.fifteenmintable.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private AuthTokenService authTokenService;

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
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ApiRespDto<String> respDto = userAuthService.signin(signinReqDto);

        String accessToken = respDto.getData();
        authTokenService.onSigninSuccess(accessToken, request, response);

        return ResponseEntity.ok(respDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiRespDto<String>> refresh(
            @CookieValue(value = "RT", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authTokenService.refresh(refreshToken, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRespDto<Void>> logout(
            @CookieValue(name = "RT", required = false) String rt,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authTokenService.logout(rt, authorization, response));
    }

}
