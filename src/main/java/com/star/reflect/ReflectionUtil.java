package com.star.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import com.star.clazz.ClassUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 反射工具类
 * 
 * @author starhq
 *
 */
public final class ReflectionUtil {

	private ReflectionUtil() {
	}

	/**
	 * 新建代理对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(final Class<T> interfaceClass, final InvocationHandler invocationHandler) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				invocationHandler);
	}

	/**
	 * 通过type得到classname
	 */
	public static String getClassName(final Type type) {
		Assert.notNull(type, "get class name by type failure,the type is null");
		String className = type.toString();
		if (className.startsWith(StringUtil.CLASS)) {
			className = StringUtil.removePrefix(className, StringUtil.CLASS);
		} else if (className.startsWith(StringUtil.INTERFACE)) {
			className = StringUtil.removePrefix(className, StringUtil.INTERFACE);
		}
		return className;
	}

	/**
	 * 根据type获得class
	 */
	public static Class<?> getClass(final Type type) {
		final String className = getClassName(type);
		return ClassUtil.loadClass(className);
	}

	/**
	 * 根据type实例化对象
	 */
	public static <T> T getInstance(final Type type) {
		final String className = getClassName(type);
		return ClassUtil.newInstance(ClassUtil.loadClass(className));
	}

}
