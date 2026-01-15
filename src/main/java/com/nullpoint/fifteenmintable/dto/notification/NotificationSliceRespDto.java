package com.nullpoint.fifteenmintable.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSliceRespDto {
    private List<NotificationRespDto> notifications;
    private Integer nextCursor;  // 더 가져올 때 사용할 마지막 notificationId (없으면 null)
}
