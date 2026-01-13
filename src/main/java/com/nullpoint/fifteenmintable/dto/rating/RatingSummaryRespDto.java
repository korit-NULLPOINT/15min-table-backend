package com.nullpoint.fifteenmintable.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingSummaryRespDto {
    private Double avgRating;
    private Integer countRating;
}
