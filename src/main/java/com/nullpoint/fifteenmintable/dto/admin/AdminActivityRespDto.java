package com.nullpoint.fifteenmintable.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityRespDto {
    private String type;          // SIGNUP | RECIPE | POST | QNA (추후 확장)
    private String action;       // CREATED | UPDATED (레시피 수정 포함)
    private Integer targetId;    // type별 대상 id (SIGNUP=userId, RECIPE=recipeId, POST=postId, QNA=qnaId)
    private String title;        // signup은 null
    private String username;
    private LocalDateTime occurredAt;    // create_dt or update_dt
}
