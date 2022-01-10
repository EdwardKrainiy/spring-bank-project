package com.itech.config;

import com.itech.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private AccountService accountService;

    /**
     * checkExpiredCreationRequests method. Scheduler, which marks all AccountCreationRequests, which was created more than 4 hours ago as EXPIRED, and checks that every 5 minutes.
     */

    @Scheduled(fixedDelayString = "${scheduler.fixed.delay.in.seconds}")
    public void checkExpiredCreationRequests() {
        accountService.checkExpiredAccountCreationRequests();
    }
}
