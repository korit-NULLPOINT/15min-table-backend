package com.nullpoint.fifteenmintable.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    private Integer subCategoryId;
    private String name;
}
