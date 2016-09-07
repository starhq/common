package com.star.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 有延迟功能的线程池
 * 
 * @author starhq
 *
 */
public final class ScheduledExecutorUtil {

	/**
	 * 默认线程池
	 */
	private static ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	private ScheduledExecutorUtil() {
	}

	/**
	 * 延迟多少时间执行
	 * 
	 */
	public static ScheduledFuture<?> execute(final Runnable runnable, final long delay, final TimeUnit unit) {
		return executor.schedule(runnable, delay, unit);
	}

	/**
	 * 延迟多少时间执行,带返回值的
	 * 
	 */
	public static <T> ScheduledFuture<T> execute(final Callable<T> callable, final long delay, final TimeUnit unit) {
		return executor.schedule(callable, delay, unit);
	}

	/**
	 * 初始延迟多少时间执行,延迟一定时间，重复执行的任务
	 * 
	 */
	public static ScheduledFuture<?> executeAtFixedRate(final Runnable command, final long initialDelay,
			final long period, final TimeUnit unit) {
		return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * 初始延迟多少时间执行,据上个任务结束，延迟一定时间，重复执行的任务
	 * 
	 */
	public static ScheduledFuture<?> executeWithFixedDelay(final Runnable command, final long initialDelay,
			final long delay, final TimeUnit unit) {
		return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	/**
	 * 重启公共线程池
	 */
	public static void restart() {
		executor.shutdownNow();
		executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	}

}
