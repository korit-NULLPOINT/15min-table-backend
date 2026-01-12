package com.nullpoint.fifteenmintable.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SigninReqDto {
    private String email;
    private String password;
}
