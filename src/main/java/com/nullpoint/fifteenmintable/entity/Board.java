package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Board {
    private Integer boardId;
    private String title;
    private Integer boardTypeId;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    private BoardType boardType;
}
