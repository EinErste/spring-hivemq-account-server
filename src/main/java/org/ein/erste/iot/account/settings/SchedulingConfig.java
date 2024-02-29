package org.ein.erste.iot.account.settings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Bean(name = "mainTaskScheduler")
    public TaskScheduler taskScheduler() {
        SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("mainTaskScheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

    @Bean(name = "mainTaskExecutor")
    public Executor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("mainTaskExecutor-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
