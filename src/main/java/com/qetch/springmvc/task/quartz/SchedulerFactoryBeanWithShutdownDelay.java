package com.qetch.springmvc.task.quartz;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 解决当tomcat已经关闭了，但是quartz的work-thread还没有杀死的bug
 * @author ZCW
 *
 */
public class SchedulerFactoryBeanWithShutdownDelay extends SchedulerFactoryBean {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerFactoryBeanWithShutdownDelay.class);
	
	/**
	 * 关于Quartz内存泄露的不太美观的解决方案：
	 * 在调用scheduler.shutdown(true)后增加延时，等待worker线程结束。
	 */
	@Override
	public void destroy() throws SchedulerException {
		super.destroy();
		try {
			logger.debug("正在销毁quartz work-thread............");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
