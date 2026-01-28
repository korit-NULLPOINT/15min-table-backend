package com.nullpoint.fifteenmintable.scheduler;

import com.nullpoint.fifteenmintable.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserDeleteScheduler {

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 0 * * * *")
    public void purgeInactiveUsers() {
        int deleted = accountService.deleteUser();
        if (deleted > 0) {
            log.info("[Scheduler] inactive user purge deleted={}", deleted);
        }
    }
}
