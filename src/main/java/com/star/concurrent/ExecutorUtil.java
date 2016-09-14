package com.star.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * 线程池工具
 * 
 * @author liushiquan,luxiaolei
 *
 */
public final class ExecutorUtil {

	/**
	 * 默认的线程池
	 */
	private static ExecutorService executor = Executors.newCachedThreadPool();

	private ExecutorUtil() {
	}

	/**
	 * 线程池中执行线程
	 * 
	 */
	public static void execute(final Runnable runnable) {
		try {
			executor.execute(runnable);
		} catch (Exception e) {
			throw new ToolException(StringUtil.format("thread pool run task failue,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 重启公共线程池
	 */
	public static void restart() {
		executor.shutdownNow();
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * 新建一个固定大小线程池
	 */
	public static ExecutorService newExecutor(final int threadSize) {
		return Executors.newFixedThreadPool(threadSize);
	}

	/**
	 * 获得一个新的线程池
	 */
	public static ExecutorService newExecutor() {
		return Executors.newCachedThreadPool();
	}

	/**
	 * 获得一个新的线程池，只有单个线程
	 */
	public static ExecutorService newSingleExecutor() {
		return Executors.newSingleThreadExecutor();
	}

	/**
	 * 执行有返回值的异步方法
	 */
	public static <T> Future<T> execAsync(final Callable<T> task) {
		return executor.submit(task);
	}

	/**
	 * 新建一个CompletionService
	 */
	public static <T> CompletionService<T> newCompletionService() {
		return new ExecutorCompletionService<T>(executor);
	}

	/**
	 * 新建一个CompletionService
	 */
	public static <T> CompletionService<T> newCompletionService(final ExecutorService executor) {
		return new ExecutorCompletionService<T>(executor);
	}

}
