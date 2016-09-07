package com.star.lang;

/**
 * 过滤器
 */
public interface Filter<T> {

	/**
	 * 有返回值灵活一点
	 */
	boolean accept(T instance);
}
