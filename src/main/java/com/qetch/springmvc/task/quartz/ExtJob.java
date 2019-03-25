package com.qetch.springmvc.task.quartz;

import java.io.Serializable;

/**
 * Quartz与Spring整合时，自定义的Job可以拥有Spring的上下文，
 * 因此定义了该接口，自定义的Job需要实现该接口，并实现executeInternal的task，
 * 这样解决了Quartz与Spring在集群环境下，可以不需要序列化，
 * 只要在executeInternal获取Spring上下文的target job bean。
 * 调用其相关的处理方法，来处理业务。
 *  
 * @author ZCW
 *
 */
public interface ExtJob extends Serializable {
	
	/**
	 * 处理任务的核心方法
	 */
	void executeInternal();
}
