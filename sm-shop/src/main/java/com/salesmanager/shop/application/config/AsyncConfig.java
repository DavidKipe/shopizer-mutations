package com.salesmanager.shop.application.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private static final int EXECUTOR_SERVICE_NUMBER_THREADS = 5;

  @Override
  public Executor getAsyncExecutor() {
				System.out.println("$#7879#"); return Executors.newFixedThreadPool(EXECUTOR_SERVICE_NUMBER_THREADS);
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
				System.out.println("$#7880#"); return new SimpleAsyncUncaughtExceptionHandler();
  }
}
