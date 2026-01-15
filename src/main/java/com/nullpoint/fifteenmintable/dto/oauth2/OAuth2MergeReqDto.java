package com.nullpoint.fifteenmintable.dto.oauth2;

import com.nullpoint.fifteenmintable.entity.OAuth2User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2MergeReqDto {
    private String email;
    private String password;
    private String provider;
    private String providerUserId;

    public OAuth2User toEntity(Integer userId) {
        return OAuth2User.builder()
                .userId(userId)
                .provider(provider)
                .providerUserId(providerUserId)
                .build();
    }
}
