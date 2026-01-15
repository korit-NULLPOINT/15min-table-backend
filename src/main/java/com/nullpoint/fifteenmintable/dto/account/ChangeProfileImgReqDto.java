package com.nullpoint.fifteenmintable.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeProfileImgReqDto {
    private Integer userId;
    private String profileImgUrl;
}
