package com.nullpoint.fifteenmintable.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRespDto {
    private Integer notificationId;
    private Integer actorUserId;
    private String actorUsername;
    private String notificationType;
    private Integer recipeId;
    private Integer isRead;
    private LocalDateTime createDt;
}
