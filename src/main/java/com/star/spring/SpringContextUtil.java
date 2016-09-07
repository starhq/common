package com.star.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 另一个版本和spring耦合有点严重，另外一个好
 * 
 * @author starhq
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext atx;

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see
	 * 
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * 
	 * (org.springframework.context.ApplicationContext)
	 * 
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		atx = applicationContext;
	}

	public static Object getBean(String beanName) {
		return atx.getBean(beanName);
	}

}
