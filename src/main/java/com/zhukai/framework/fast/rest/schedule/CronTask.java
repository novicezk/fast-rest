package com.zhukai.framework.fast.rest.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CronTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CronTask.class);

	private CronSequenceGenerator sequenceGenerator;
	private Runnable runnable;
	private ScheduledThreadPoolExecutor executor;

	public CronTask(String expression, Runnable runnable, ScheduledThreadPoolExecutor executor) {
		sequenceGenerator = new CronSequenceGenerator(expression);
		this.runnable = runnable;
		this.executor = executor;
	}

	@Override
	public void run() {
		ScheduledFuture future = executor.schedule(runnable, nextExecutionTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		try {
			future.get();
			run();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Scheduled thread pool run error", e);
		}
	}

	private long nextExecutionTime() {
		return this.sequenceGenerator.next(new Date(System.currentTimeMillis())).getTime();
	}

}
