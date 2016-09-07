package com.star.concurrent;

import java.util.HashMap;
import java.util.Map;

import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * 线程基类,封装了一些额外的方法,如运行时长，调用次数等
 * 
 * @author starhq
 *
 */
public abstract class AbstractRunnable implements Runnable {

	/** 此线程的标识ID */
	protected transient long threadId;
	/** 此线程的名称，默认为空串 */
	protected String name = "";
	/** 线程是否启动标志 */
	protected transient boolean running;
	/** 被调用的次数（包括当前正在运行的） */
	protected int callCount;
	/** 此线程每次运行的时长，key为次数，value为时长 */
	protected transient Map<Integer, Long> runTimes = new HashMap<Integer, Long>();

	/**
	 * 构造方法
	 */
	public AbstractRunnable() {
		// nothing
	}

	/**
	 * 构造方法
	 */
	public AbstractRunnable(final long threadId, final String name) {
		this.threadId = threadId;
		this.name = name;
	}

	/**
	 * 线程启动
	 */
	@Override
	public void run() {
		if (running) {
			throw new ToolException(StringUtil
					.format("** 【 {} 】thread {} is running，plz stop the running thread or wait **", threadId, name));
		}
		running = true;
		callCount++;
		final long timesBefore = System.currentTimeMillis();
		work();
		runTimes.put(callCount, System.currentTimeMillis() - timesBefore);
		running = false;
	}

	/**
	 * 具体实现，交给子类
	 */
	public abstract void work();

	/**
	 * 是否在运行
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 停止运行
	 */
	public void stopRunning() {
		this.running = false;
	}

	/**
	 * 获得
	 * 
	 * @return
	 */
	public Map<Integer, Long> getRunTimes() {
		return runTimes;
	}

	/**
	 * 被调用的次数
	 * 
	 * @return
	 */
	public int getCallCount() {
		return callCount;
	}

	/**
	 * 设置调用的次数
	 */
	public void setCallCount(final int callCount) {
		this.callCount = callCount;
	}

	/**
	 * 获得线程名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置线程名
	 * 
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * 获得线程id
	 * 
	 * @return
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * 设置线程id
	 * 
	 * @param threadId
	 */
	public void setThreadId(final long threadId) {
		this.threadId = threadId;
	}

	/**
	 * 简单重写hashcode
	 */
	@Override
	public int hashCode() {
		return (int) threadId;
	}

	/**
	 * 简单重写equals
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean result;
		if (obj instanceof AbstractRunnable) {
			result = this.threadId == ((AbstractRunnable) obj).threadId;
		} else {
			result = false;
		}
		return result;
	}

}
