package com.zhukai.framework.fast.rest.schedule;

import com.zhukai.framework.fast.rest.annotation.extend.Scheduled;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.factory.ExecutorFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskTrigger {
	private static final Logger logger = LoggerFactory.getLogger(TaskTrigger.class);
	private static final ScheduledThreadPoolExecutor executor = ExecutorFactory.getScheduledTaskExecutor();

	public static void registerTask(Method method) {
		Object bean = ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass());
		Scheduled scheduled = method.getAnnotation(Scheduled.class);
		long fixedRate = scheduled.fixedRate();
		long fixedDelay = scheduled.fixedDelay();
		String cron = scheduled.cron();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					method.invoke(bean);
				} catch (Exception e) {
					logger.error("Scheduled task exec error", e);
				}
			}
		};
		if (fixedRate > 0) {
			executor.scheduleAtFixedRate(runnable, fixedRate, fixedRate, scheduled.timeUnit());
			logger.info("Schedule rate task, method:{}", method);
		} else if (fixedDelay > 0) {
			executor.schedule(runnable, fixedDelay, scheduled.timeUnit());
			logger.info("Schedule delay task, method:{}", method);
		} else if (StringUtils.isNotBlank(cron)) {
			CronTask cronTask = new CronTask(cron, runnable, executor);
			executor.execute(cronTask);
			logger.info("Schedule cron task, method:{}, cron:{}", method, cron);
		}

	}

}
