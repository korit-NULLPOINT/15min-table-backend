package com.nullpoint.fifteenmintable.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeUsernameReqDto {
    private Integer userId;
    private String username;
}
