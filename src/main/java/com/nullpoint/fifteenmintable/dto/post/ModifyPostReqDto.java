package com.nullpoint.fifteenmintable.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPostReqDto {
    private String title;
    private String content;
    private List<String> imgUrls;

    public boolean hasImgUrlsField() {
        return imgUrls != null;
    }
}
