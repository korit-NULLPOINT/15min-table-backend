package com.nullpoint.fifteenmintable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRespDto<T> {
    private String status;
    private String message;

    @Schema(description = "응답 데이터(없으면 null)", nullable = true)
    private T data;
}
