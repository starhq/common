package com.star.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 泛型工具类
 * 
 * @author starhq
 *
 */
public final class GenericUtils {

	private GenericUtils() {
	}

	/**
	 * 获得指定类的父类的泛型参数的实际类型
	 */
	public static Type getSuperClassGenricType(final Class<?> clazz, final int index) {
		Assert.notNull(clazz, "get parent genertic type failure,the clazz is null");
		final Type type = clazz.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			throw new ToolException(StringUtil.format(
					"get parent generic type failue: Class {}'s parent not support generic", clazz.getSimpleName()));
		}
		final Type[] params = ((ParameterizedType) type).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			throw new ToolException(StringUtil.format("get parent's generic type failure:the index you input {}",
					index < 0 ? "must not less than zero" : "bigger than field's count"));
		}
		return params[index];
	}

	/**
	 * 获得方法返回值泛型参数的实际类型
	 */
	public static Type getMethodGenericReturnType(final Method method, final int index) {
		Assert.notNull(method, "get method return object's generic type failure,the method is null");
		final Type returnType = method.getGenericReturnType();
		if (!(returnType instanceof ParameterizedType)) {
			throw new ToolException(
					"get method return object's generic type failure,the method return objects not support generic");
		}
		final ParameterizedType type = (ParameterizedType) returnType;
		final Type[] typeArguments = type.getActualTypeArguments();
		if (index >= typeArguments.length || index < 0) {
			throw new ToolException(
					StringUtil.format("get method return object's generic type failure:the index you input {}",
							index < 0 ? "must not less than zero" : "bigger than field's count"));
		}
		return typeArguments[index];
	}

	/**
	 * 获得方法签名泛型参数的实际类型
	 */
	public static List<Class<?>> getMethodGenericParameterTypes(final Method method, final int index) {
		Assert.notNull(method, "get method's generic type failure,the method is null");
		final Type[] parameterTypes = method.getGenericParameterTypes();
		if (index >= parameterTypes.length || index < 0) {
			throw new ToolException(StringUtil.format("get method's generic type failure:the index you input {}",
					index < 0 ? "must not less than zero" : "bigger than field's count"));
		}
		final Type parameterType = parameterTypes[index];
		if (!(parameterType instanceof ParameterizedType)) {
			throw new ToolException("get method's generic type failure,the method's field not support generic");
		}
		final ParameterizedType aType = (ParameterizedType) parameterType;
		final Type[] parameterArgTypes = aType.getActualTypeArguments();
		final List<Class<?>> results = new ArrayList<Class<?>>(parameterArgTypes.length);
		for (final Type parameterArgType : parameterArgTypes) {
			final Class<?> parameterArgClass = (Class<?>) parameterArgType;
			results.add(parameterArgClass);
		}
		return results;
	}

	/**
	 * 获得变量泛型参数的实际类型
	 */
	public static Type getFieldGenericType(final Field field, final int index) {
		Assert.notNull(field, "get filed's generic type failure,the field is null");
		final Type fieldType = field.getGenericType();
		if (!(fieldType instanceof ParameterizedType)) {
			throw new ToolException("get filed's generic type failure,the field not support generic");
		}
		final ParameterizedType aType = (ParameterizedType) fieldType;
		final Type[] fieldArgTypes = aType.getActualTypeArguments();
		if (index >= fieldArgTypes.length || index < 0) {
			throw new ToolException(StringUtil.format("get filed's generic type failure:the index you input {}",
					index < 0 ? "must not less than zero" : "bigger than field's count"));
		}
		return fieldArgTypes[index];
	}
}