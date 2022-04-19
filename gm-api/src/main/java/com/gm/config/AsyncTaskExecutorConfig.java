package com.gm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncTaskExecutorConfig {

	@Bean
	public ThreadPoolTaskExecutor taskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int processors = Runtime.getRuntime().availableProcessors();
		executor.setCorePoolSize(processors*2);
		executor.setMaxPoolSize(processors * 4);
		executor.setQueueCapacity(processors * 4);//修改线程池队列长度
		executor.setKeepAliveSeconds(30);//保留30s
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());//队列满后拒绝策略
		//该值为true，则线程池数量最后销毁到0个;false销毁机制：超过核心线程数时，而且（超过最大值或者timeout过），就会销毁。add by Daniel 2018/04/16
		executor.setAllowCoreThreadTimeOut(false);
		executor.setWaitForTasksToCompleteOnShutdown(true);//等待所有线程结束再关闭
		return executor;
	}
}
