package com.weeg.configurer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName:ThreadConfig
 * @description: 线程池配置
 * @author: SJ
 * @date: 2020/8/8
 */
@Configuration
@EnableAsync
public class ThreadConfig {

    @Value("${threadpool.core-pool-size}")
    private int corePoolSize;

    @Value("${threadpool.max-pool-size}")
    private int maxPoolSize;

    @Value("${threadpool.queue-capacity}")
    private int queueCapacity;

    @Value("${threadpool.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        // 空闲的多余线程最大存活时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-resource-schedule-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
