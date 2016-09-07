package com.star.net.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cookie池 做缓存用
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public final class CookiePool {

	/**
	 * key: host, value: cookies字符串
	 */
	private static Map<String, String> cookies = new ConcurrentHashMap<>();

	private CookiePool() {
	}

	/**
	 * 
	 * 获得某个网站的Cookie信息
	 */
	public static String get(final String host) {
		return cookies.get(host);
	}

	/**
	 * 
	 * 将某个网站的Cookie放入Cookie池
	 * 
	 */
	public static void put(final String host, final String cookie) {
		cookies.put(host, cookie);
	}
}
