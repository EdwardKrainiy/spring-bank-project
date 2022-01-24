package com.itech.config;

import com.itech.service.account.AccountService;
import com.itech.utils.literal.PropertySourceClasspath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * SchedulerConfig class. Provides us Scheduler, which checks EXPIRED Account CreationRequests and marks them.
 */

@Configuration
@EnableScheduling
@PropertySource(PropertySourceClasspath.SCHEDULER_PROPERTIES_CLASSPATH)
@RequiredArgsConstructor
public class SchedulerConfig {

    private final AccountService accountService;

    /**
     * checkExpiredCreationRequests method. Scheduler, which marks all AccountCreationRequests, which was created more than 4 hours ago as EXPIRED, and checks that every 5 minutes.
     */

    @Scheduled(fixedDelayString = "${scheduler.fixed.delay.in.seconds}")
    public void checkExpiredCreationRequests() {
        accountService.checkExpiredAccountCreationRequests();
    }
}
