package com.thirdeye.holdedstockviewer.multithreading;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class HoldedStockAsync {
	
	@Bean(name="HoldedStockAsynchThread")
	public Executor getThreadPoolExecutor()
	{
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(26);
		executor.setThreadNamePrefix("HoldedStock-");
		executor.initialize();
		return executor;
	}
}
