package com.e6.e9.commons;

import org.apache.log4j.Logger;

import weaver.interfaces.schedule.BaseCronJob;

/**
 * 
 * 描述:定时器封装
 * 
 * @author HM
 * @date 2022年5月4日
 *
 */
public abstract class AbstractCronJob extends BaseCronJob {

	private boolean lastTaskStatus = false; // true 表示正在执行上一轮任务

	private Logger log = Logger.getLogger(this.getClass());

	@Override
	public void execute() {
		if (lastTaskStatus) {
			log.error("上一轮计划任务未结束！");
		} else {
			try {
				log.info("计划任务开始...");
				lastTaskStatus = true;
				start();
			} catch (Exception e) {
				log.error("计划任务异常", e);
			} finally {
				lastTaskStatus = false;
				log.info("计划任务结束。");
			}
		}
	}

	public Logger log() {
		return log;
	}

	public abstract void start() throws Exception;
}