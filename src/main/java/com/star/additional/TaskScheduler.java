package com.star.additional;

import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务工具类
 * 
 * @author starhq
 *
 */
public final class TaskScheduler extends Timer {

	/**
	 * daemon进程
	 */
	private TaskScheduler(final boolean isDaemon) {
		super(isDaemon);
	}

	/**
	 * 实例化一个新对象
	 */
	public static TaskScheduler newInstance(final boolean isDaemon) {
		return new TaskScheduler(isDaemon);
	}

	/**
	 * 于指定时间启动，启动间隔为period/1000秒
	 */
	public void schedule(final TimerTask timerTask, final Date startTime, final long period) {
		super.scheduleAtFixedRate(timerTask, Objects.isNull(startTime) ? new Date() : startTime, period);
	}

	/**
	 * 延迟delay/1000秒启动，启动间隔为period/1000秒
	 */
	public void schedule(final TimerTask timerTask, final long delay, final long period) {
		super.scheduleAtFixedRate(timerTask, delay, period);
	}
}
