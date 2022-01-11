package com.itech.config;

import com.itech.service.account.AccountService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * SchedulerConfig class. Provides us Scheduler, which checks EXPIRED Account CreationRequests and marks them.
 */

@Configuration
@EnableScheduling
@PropertySource("classpath:properties/scheduler.properties")
public class SchedulerConfig {

    private final AccountService accountService;

    public SchedulerConfig(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * checkExpiredCreationRequests method. Scheduler, which marks all AccountCreationRequests, which was created more than 4 hours ago as EXPIRED, and checks that every 5 minutes.
     */

    @Scheduled(fixedDelayString = "${scheduler.fixed.delay.in.seconds}")
    public void checkExpiredCreationRequests() {
        accountService.checkExpiredAccountCreationRequests();
    }
}
