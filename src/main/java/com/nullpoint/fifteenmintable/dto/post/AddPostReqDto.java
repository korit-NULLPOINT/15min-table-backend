package com.nullpoint.fifteenmintable.dto.post;

import com.nullpoint.fifteenmintable.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPostReqDto {
    private String title;
    private String content;
    private List<String> imgUrls;

    public Post toEntity(Integer boardId, Integer userId) {
        return Post.builder()
                .boardId(boardId)
                .userId(userId)
                .title(title)
                .content(content)
                .build();
    }
}
