package com.nullpoint.fifteenmintable.dto.ai;

import lombok.Data;

@Data
public class AiHashtagReqDto {
    private String title;
    private String intro;
    private String ingredients;
    private String steps;
    private Integer limit;
}
