package ru.tbank.springapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public ExecutorService fetchingExecutorService(ApplicationConfig config) {
        return Executors.newFixedThreadPool(config.dataFetching().fetchingExecutorServiceThreadsNumber());
    }

}
