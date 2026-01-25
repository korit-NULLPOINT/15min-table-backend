package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.notification.NotificationRespDto;
import com.nullpoint.fifteenmintable.entity.Notification;
import com.nullpoint.fifteenmintable.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository {

    @Autowired
    private NotificationMapper notificationMapper;

    public int createNotification(Notification notification) {
        return notificationMapper.createNotification(notification);
    }

    public int markAsRead(Integer notificationId, Integer receiverUserId) {
        return notificationMapper.markAsRead(notificationId, receiverUserId);
    }

    public int markAllAsRead(Integer receiverUserId) {
        return notificationMapper.markAllAsRead(receiverUserId);
    }

    public int getUnreadCount(Integer receiverUserId) {
        return notificationMapper.getUnreadCount(receiverUserId);
    }

    public List<NotificationRespDto> getNotifications(
            Integer receiverUserId, Integer cursor, Integer size, String mode, Integer readKeepDays
    ) {
        return notificationMapper.getNotifications(receiverUserId, cursor, size, mode, readKeepDays);
    }

}
