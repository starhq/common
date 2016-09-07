package com.star.lang;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.star.collection.ArrayUtil;
import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * 断言
 * 
 * @author Looly
 *
 */
public final class Assert {

	private Assert() {
	}

	/**
	 * 断言是否为正
	 */
	public static void isTrue(final boolean expression, final String message) {
		if (!expression) {
			throw new ToolException(StringUtil.isBlank(message) ? "expression must be true" : message);
		}
	}

	/**
	 * 断言对象为空
	 */
	public static void isNull(final Object object, final String message) {
		if (!Objects.isNull(object)) {
			throw new ToolException(StringUtil.isBlank(message) ? "object must be null" : message);
		}
	}

	/**
	 * 断言对象不为空
	 */
	public static void notNull(final Object object, final String message) {
		if (Objects.isNull(object)) {
			throw new ToolException(StringUtil.isBlank(message) ? "object must be null" : message);
		}
	}

	/**
	 * 字符串不能为空
	 */
	public static void notEmpty(final String text, final String message) {
		if (StringUtil.isEmpty(text)) {
			throw new ToolException(StringUtil.isBlank(message) ? "string must not be empty" : message);
		}
	}

	/**
	 * 字符串不能为空白
	 */
	public static void notBlank(final String text, final String message) {
		if (StringUtil.isBlank(text)) {
			throw new ToolException(StringUtil.isBlank(message) ? "string must not be blank" : message);
		}
	}

	/**
	 * textToSearch不能包含substring,好像作用不大啊
	 */
	public static void notContain(final String textToSearch, final String substring, final String message) {
		if (!StringUtil.isEmpty(textToSearch) && !StringUtil.isEmpty(substring) && !textToSearch.contains(substring)) {
			throw new ToolException(StringUtil.isBlank(message)
					? StringUtil.format("string {} must not include sub string {}", textToSearch, substring) : message);
		}
	}

	/**
	 * 数组不能为空
	 */
	public static void notEmpty(final Object[] array, final String message) {
		if (ArrayUtil.isEmpty(array)) {
			throw new ToolException(StringUtil.isBlank(message) ? "array must not be empty" : message);
		}
	}

	/**
	 * 数组中不能包含空
	 */
	public static void noNullElements(final Object[] array, final String message) {
		if (ArrayUtil.isEmpty(array)) {
			for (final Object element : array) {
				if (Objects.isNull(element)) {
					throw new ToolException(StringUtil.isBlank(message) ? "array must not has null element " : message);
				}
			}
		}
	}

	/**
	 * 集合不能为空
	 */
	public static void notEmpty(final Collection<?> collection, final String message) {
		if (CollectionUtil.isEmpty(collection)) {
			throw new ToolException(StringUtil.isBlank(message) ? "collection must not be empty" : message);
		}
	}

	/**
	 * map不能为空
	 */
	public static void notEmpty(final Map<?, ?> map, final String message) {
		if (CollectionUtil.isEmpty(map)) {
			throw new ToolException(StringUtil.isBlank(message) ? "map must not be empty" : message);
		}
	}

	/**
	 * obj一定要是type的实例
	 */
	public static void isInstanceOf(final Class<?> type, final Object obj, final String message) {
		notNull(type, "type must not be null");
		if (!type.isInstance(obj)) {
			throw new ToolException((StringUtil.isBlank(message)
					? StringUtil.format("object {} must be Class {}'s instance", obj.getClass().getName(), type)
					: message));
		}
	}

	/**
	 * superType一定是subType的父类
	 */
	public static void isAssignable(final Class<?> superType, final Class<?> subType, final String message) {
		notNull(superType, "parent class must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new ToolException((StringUtil.isBlank(message)
					? StringUtil.format("Class {} must be class {}'s parent", superType, subType) : message));
		}
	}

}
