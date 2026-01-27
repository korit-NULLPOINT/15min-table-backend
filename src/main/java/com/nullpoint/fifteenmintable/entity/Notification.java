package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private Integer notificationId;
    private Integer receiverUserId;
    private Integer actorUserId;
    private String notificationType;
    private String targetType;
    private Integer targetId;
    private Integer commentId;
    private Integer  isRead;
    private LocalDateTime createDt;
}
