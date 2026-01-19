package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2MergeReqDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2SignupReqDto;
import com.nullpoint.fifteenmintable.service.OAuth2AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2AuthController {

    @Autowired
    private OAuth2AuthService oAuth2AuthService;

    @PostMapping("/signup")
    public ResponseEntity<ApiRespDto<Void>> signup(@RequestBody OAuth2SignupReqDto oAuth2SignupReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.signup(oAuth2SignupReqDto));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiRespDto<Void>> merge(@RequestBody OAuth2MergeReqDto oAuth2MergeReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.merge(oAuth2MergeReqDto));
    }

    // http://localhost:8080/oauth2/authorization/[naver or google 택1] -> providerUserId 확인
}










