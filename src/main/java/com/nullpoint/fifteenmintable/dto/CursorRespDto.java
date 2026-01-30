package com.nullpoint.fifteenmintable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorRespDto<T> {
    private List<T> items;
    private Boolean hasNext;
    private String nextCursor;
}
