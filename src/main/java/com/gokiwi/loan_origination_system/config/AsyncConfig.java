package com.gokiwi.loan_origination_system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async processing configuration for Form Service
 * Provides optimized thread pools for different types of processing
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Thread pool for form processing operations
     */
    @Bean("formProcessingExecutor")
    public Executor formProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("FormProcessing-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        log.info("Form processing executor initialized with core pool size: {}, max pool size: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }

    /**
     * Thread pool for loan processing operations
     */
    @Bean("loanProcessingExecutor")
    public Executor loanProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("LoanProcessing-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        log.info("Loan processing executor initialized with core pool size: {}, max pool size: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }

    /**
     * Thread pool for validation operations
     */
    @Bean("validationExecutor")
    public Executor validationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(180);
        executor.setThreadNamePrefix("Validation-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        log.info("Validation executor initialized with core pool size: {}, max pool size: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }
}