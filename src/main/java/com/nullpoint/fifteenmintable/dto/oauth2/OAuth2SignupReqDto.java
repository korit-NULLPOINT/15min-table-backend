package com.nullpoint.fifteenmintable.dto.oauth2;
import com.nullpoint.fifteenmintable.entity.OAuth2User;
import com.nullpoint.fifteenmintable.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class OAuth2SignupReqDto {
    private String email;
    private String password;
    private String username;
    private String provider;
    private String providerUserId;

    public User toUserEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .build();
    }

    public OAuth2User toOAuth2UserEntity(int userId) {
        return OAuth2User.builder()
                .userId(userId)
                .provider(provider)
                .providerUserId(providerUserId)
                .build();
    }
}
