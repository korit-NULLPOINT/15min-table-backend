package com.nullpoint.fifteenmintable.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninReqDto {
    private String email;
    private String password;
}
