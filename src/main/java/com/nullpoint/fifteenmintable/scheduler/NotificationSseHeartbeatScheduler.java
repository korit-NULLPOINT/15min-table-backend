package com.nullpoint.fifteenmintable.scheduler;

import com.nullpoint.fifteenmintable.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationSseHeartbeatScheduler {

    @Autowired
    private NotificationSseService notificationSseService;

    @Scheduled(fixedRate = 25000)
    public void heartbeat() {
        notificationSseService.heartbeatAll();
    }
}
