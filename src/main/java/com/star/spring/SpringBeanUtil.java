package com.star.spring;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * 获取spring容器托管的bean
 * 
 * @author starhq
 *
 */
public final class SpringBeanUtil {

	/**
	 * 锁
	 */
	private static Object obj = new Object();

	private SpringBeanUtil() {
	}

	/**
	 * 按名字查找bean
	 */
	public static Object getBeanFromSpring(final String beanName) {
		synchronized (obj) {
			final WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			return wac.getBean(beanName);
		}
	}
}
