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


    @PostMapping("/logout")
    public ResponseEntity<ApiRespDto<Void>> logout(
            @CookieValue(value = "RT", required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = jwtAuthenticationFilter.resolveAccessToken(request);
        return ResponseEntity.ok(authTokenService.logout(refreshToken, accessToken, response));
    }
}
