package com.qetch.springmvc.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.qetch.springmvc.common.Constants;
import com.qetch.springmvc.task.quartz.ExtJob;

@Component
public class SecondTask implements ExtJob {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SecondTask.class); 

	@Override
	public void executeInternal() {
		try {
			logger.info("**********开始执行任务（线程数统计：" + Thread.getAllStackTraces().size() + "）**********");
			long starttime = System.currentTimeMillis();
			// 执行任务
			process();
			logger.info("**********结束执行任务（线程数统计：" + Thread.getAllStackTraces().size() + "，共耗时：" + (System.currentTimeMillis() - starttime) + "）**********");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void process() {
		List<String> wxcidSet = Arrays.asList("111", "222");
		
		Collection<Callable<Map<String, Long>>> tasks = 
				Collections2.transform(wxcidSet, new Function<String, Callable<Map<String, Long>>>() {

					@Override
					public Callable<Map<String, Long>> apply(String wxcid) {
						return toCallable(wxcid);
					}
		});
		
		int size = wxcidSet.size();
		int poolSize = Runtime.getRuntime().availableProcessors() * Constants.MERGE_CPU_POOL_SIZE;
		logger.info(String.format("等待同步微信客户.size=%d,线程数量=%d", size, poolSize));
		
		ExecutorService service = Executors.newFixedThreadPool(poolSize);
		try {
			List<Future<Map<String, Long>>> results = service.invokeAll(tasks, 30, TimeUnit.MINUTES);
			for (Future<Map<String, Long>> future : results) {
				try {
					logger.debug(future.get().toString());
				} catch (ExecutionException e) {
					logger.error("sync error", e);
				}
			}
		} catch (Exception e) {
			logger.error("同步出错：", e);
		} finally {
			service.shutdown();
		}
	}
	
	// Map<wxcid, 任务同步耗费时间>
	private Callable<Map<String, Long>> toCallable(final String wxcid) {
		return new Callable<Map<String,Long>>() {

			@Override
			public Map<String, Long> call() throws Exception {
				long oneStime = System.nanoTime();
				logger.debug("**********同步微信CID：" + wxcid + "**********");
				
				try {
					// 定时任务业务处理
					logger.info("--->secondTask--->");
					
				} catch (Exception e) {
					logger.info(wxcid + "设置数据源异常！", e);
					return Collections.singletonMap(wxcid, 0L);
				}
				
				logger.debug("**********End同步微信CID：" + wxcid + "**********");
				long oneEtime = System.nanoTime();
				long milliSeconds = TimeUnit.NANOSECONDS.toMillis(oneEtime - oneStime);
				return Collections.singletonMap(wxcid, milliSeconds);
			}
		};
	}
}