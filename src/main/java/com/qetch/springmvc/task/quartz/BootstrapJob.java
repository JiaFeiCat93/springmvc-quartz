package com.qetch.springmvc.task.quartz;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;

/**
 * 引导Job，通过Spring容器获取任务的Job，根据注入的targetJob，该Job必须实现Job2接口。
 * @author ZCW
 *
 */
public class BootstrapJob implements Serializable {
	private static final long serialVersionUID = 1L;
	private String targetJob;
	
	public void executeInternal(ApplicationContext context) {
		ExtJob job = context.getBean(ExtJob.class);
		job.executeInternal();
	}

	public String getTargetJob() {
		return targetJob;
	}
	public void setTargetJob(String targetJob) {
		this.targetJob = targetJob;
	}
}
