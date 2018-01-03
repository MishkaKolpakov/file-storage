package ua.softserve.academy.kv030.authservice.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Created by user on 08.12.17.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    private ThreadPoolTaskScheduler taskScheduler;

    private Logger logger;

    @Autowired
    public SchedulingConfig(Logger logger) {
        this.logger = logger;

        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setErrorHandler(t -> this.logger.error("Exception in @Scheduled task. ", t));
        taskScheduler.setThreadNamePrefix("@scheduled-");
        taskScheduler.initialize();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
    }
}
