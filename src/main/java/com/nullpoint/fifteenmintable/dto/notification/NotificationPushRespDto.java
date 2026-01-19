package com.nullpoint.fifteenmintable.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPushRespDto {
    private Integer notificationId;
    private String type;        // "FOLLOW" | "RECIPE_POST"
    private Integer actorUserId;
    private Integer recipeId;
    private Integer commentId;
}
