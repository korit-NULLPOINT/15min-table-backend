package com.nullpoint.fifteenmintable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRespDto<T> {
    private String status;
    private String message;
    private T data;
}
