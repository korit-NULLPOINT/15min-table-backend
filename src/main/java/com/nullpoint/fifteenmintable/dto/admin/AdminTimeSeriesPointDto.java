package com.nullpoint.fifteenmintable.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTimeSeriesPointDto {
    private String date;
    private Long count;
}
