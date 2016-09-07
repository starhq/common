package com.star.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 属性工具类
 * 
 * @author starhq
 *
 */
public final class FieldUtil {

	private FieldUtil() {
	}

	/**
	 * 
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 * 
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		final Field field = getDeclaredField(object, fieldName);

		Assert.notNull(field, StringUtil.format("colud not get filed {} on target {}", fieldName, object));

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			throw new ToolException(
					StringUtil.format("get filed {}'s value failure,the reason is: {}", fieldName, e.getMessage()), e);
		}
		return result;
	}

	/**
	 * 
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 * 
	 */
	public static void setFieldValue(final Object object, final String fieldName, final Object value) {
		final Field field = getDeclaredField(object, fieldName);

		Assert.notNull(field, StringUtil.format("colud not get filed {} on target {}", fieldName, object));

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new ToolException(
					StringUtil.format("set filed {}'s value failure,the reason is: {}", fieldName, e.getMessage()), e);
		}
	}

	/**
	 * 
	 * 循环向上转型,获取对象的DeclaredField.
	 * 
	 */
	public static Field getDeclaredField(final Object object, final String fieldName) {
		return getDeclaredField(object.getClass(), fieldName);
	}

	/**
	 * 
	 * 循环向上转型,获取类的DeclaredField.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Field getDeclaredField(final Class clazz, final String fieldName) {
		Assert.notNull(clazz, "get declared filed failure,the clazz is null");
		Assert.notBlank(fieldName, "get declared filed failure,the fieldName is blank");
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				throw new ToolException(
						StringUtil.format("get declared filed fialure,the reason is: {}", e.getMessage()), e);
			}
		}
		return null;
	}

	/**
	 * 
	 * 强制转换fileld可访问.
	 * 
	 */
	public static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}
}
