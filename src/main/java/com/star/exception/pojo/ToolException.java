package com.star.exception.pojo;

/**
 * 简单封装工具方法抛出的异常
 * 
 * @author starhq
 *
 */
public class ToolException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 没花头简单的构造方法
	 */
	public ToolException(final String message) {
		super(message);
	}

	/**
	 * 异常链
	 */
	public ToolException(String message, Throwable cause) {
		super(message, cause);
	}

}
