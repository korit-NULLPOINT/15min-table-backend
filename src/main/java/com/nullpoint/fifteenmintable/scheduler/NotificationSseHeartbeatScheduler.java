package com.nullpoint.fifteenmintable.scheduler;

import com.nullpoint.fifteenmintable.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSseHeartbeatScheduler {

    private final NotificationSseService notificationSseService;

    @Scheduled(fixedRate = 25000)
    public void heartbeat() {
        notificationSseService.heartbeatAll();
    }
}
