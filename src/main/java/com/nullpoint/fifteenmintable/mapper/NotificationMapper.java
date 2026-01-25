package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.notification.NotificationRespDto;
import com.nullpoint.fifteenmintable.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    int createNotification(Notification notification);
    int markAsRead(@Param("notificationId") Integer notificationId,
                   @Param("receiverUserId") Integer receiverUserId);
    int markAllAsRead(Integer receiverUserId);
    int getUnreadCount(Integer receiverUserId);
    List<NotificationRespDto> getNotifications(
            @Param("receiverUserId") Integer receiverUserId,
            @Param("cursor") Integer cursor,
            @Param("size") Integer size,
            @Param("mode") String mode,
            @Param("readKeepDays") Integer readKeepDays
    );
}
