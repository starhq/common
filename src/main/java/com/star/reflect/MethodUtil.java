package com.star.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.lang.Filter;
import com.star.string.StringUtil;

/**
 * 方法工具类
 * 
 * @author starhq
 *
 */
public final class MethodUtil {

	private MethodUtil() {
	}

	/**
	 * 在类、父类、接口中查找公共方法
	 */
	public static Method findMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
		Assert.notNull(clazz, "get class's method is failure,the clazz is null");
		Assert.notBlank(methodName, "get class's method is failure,the methodName is blank");
		Method method;
		try {
			method = clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			method = findDeclaredMethod(clazz, methodName, paramTypes);
		}
		return method;
	}

	/**
	 * 在类自身中的所有方法中查找指定方法
	 */
	public static Method findDeclaredMethod(final Class<?> clazz, final String methodName,
			final Class<?>... parameterTypes) {
		Assert.notNull(clazz, "get class's declared method is failure,the clazz is null");
		Assert.notBlank(methodName, "get declared class's method is failure,the methodName is blank");
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException ex) {
			if (Objects.isNull(clazz.getSuperclass())) {
				method = findDeclaredMethod(clazz.getSuperclass(), methodName, parameterTypes);
			}
		}
		return method;
	}

	/**
	 * 获得指定类中的Public方法名
	 */
	public static Set<String> getMethodNames(final Class<?> clazz) {
		Assert.notNull(clazz, "get clazz's public method name failure,the clazz is null");
		final Method[] methodArray = clazz.getMethods();
		final Set<String> methodSet = CollectionUtil.getHashSet(methodArray.length);
		for (final Method method : methodArray) {
			methodSet.add(method.getName());
		}
		return methodSet;
	}

	/**
	 * 从指定类获得方法，有过滤器
	 */
	public static List<Method> getMethods(final Class<?> clazz, final Filter<Method> filter) {
		Assert.notNull(clazz, "get clazz's method failure,the clazz is null");
		final Method[] methods = clazz.getMethods();
		List<Method> result;
		if (Objects.isNull(filter)) {
			result = Arrays.asList(methods);
		} else {
			result = CollectionUtil.getList(methods.length);
			for (final Method method : result) {
				if (filter.accept(method)) {
					result.add(method);
				}
			}
		}
		return result;
	}

	/**
	 * 反射调用方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(final Object obj, final Method method, final Object... args) {
		Assert.notNull(obj, "invoke method failure,the obj is null");
		Assert.notNull(method, "invoke method failure,the method is null");
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			return (T) method.invoke(Modifier.isStatic(method.getModifiers()) ? null : obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ToolException(StringUtil.format("invoke class {}'s method {} failue,the reason is: {}",
					obj.getClass().getSimpleName(), method.getName(), e.getMessage()), e);
		}
	}

}
