package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.auth.SigninReqDto;
import com.nullpoint.fifteenmintable.dto.auth.SignupReqDto;
import com.nullpoint.fifteenmintable.security.cookie.SseCookieUtils;
import com.nullpoint.fifteenmintable.service.UserAuthService;
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
}
