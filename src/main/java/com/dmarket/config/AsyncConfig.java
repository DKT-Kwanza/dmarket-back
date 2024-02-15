package com.dmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setThreadNamePrefix("async-pool");
        asyncExecutor.setCorePoolSize(300);
        asyncExecutor.setMaxPoolSize(600);
        asyncExecutor.setQueueCapacity(100);
        asyncExecutor.initialize();
        return asyncExecutor;
    }
}
