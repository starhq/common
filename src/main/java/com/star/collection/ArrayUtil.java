package com.star.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.star.lang.Assert;
import com.star.lang.Filter;

/**
 * 数组工具类
 * 
 * @author starhq
 *
 */
public final class ArrayUtil {

	private ArrayUtil() {
	}

	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 * 
	 * jdk1.8用stringjoiner
	 */
	public static <T> String join(final T[] array, final char conjunction) {
		Assert.notEmpty(array, "array join to string failure,the array is empty");
		final StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (final T item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(conjunction);
			}
			builder.append(item);
		}
		return builder.toString();
	}

	/**
	 * 生成一个新的重新设置大小的数组
	 */
	public static <T> T[] resize(final T[] buffer, final int newSize, final Class<?> componentType) {
		Assert.notNull(buffer, "resize array failure,the array is null");
		final T[] newArray = newArray(componentType, newSize);
		System.arraycopy(buffer, 0, newArray, 0, buffer.length >= newSize ? newSize : buffer.length);
		return newArray;
	}

	/**
	 * 新建一个空数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(final Class<?> componentType, final int newSize) {
		Assert.notNull(componentType, "create new array failure,the componentType is null");
		Assert.isTrue(newSize >= 0, "create new array failure,the newSize is negative");
		return (T[]) Array.newInstance(componentType, newSize);
	}

	/**
	 * 组合数组
	 */
	@SafeVarargs
	public static <T> T[] addAll(final T[]... arrays) {
		Assert.notNull(arrays, "merage array failue,the array is null");
		int length = 0;
		for (final T[] array : arrays) {
			if (isEmpty(array)) {
				continue;
			}
			length += array.length;
		}

		final T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

		length = 0;

		for (final T[] array : arrays) {
			if (isEmpty(array)) {
				continue;
			}

			System.arraycopy(array, 0, result, length, array.length);

			length += array.length;
		}

		return result;
	}

	/**
	 * 数组是否包含元素
	 */
	public static <T> boolean contains(final T[] array, final T value) {
		boolean result = false;
		if (!isEmpty(array)) {
			final Class<?> componetType = array.getClass().getComponentType();
			final boolean isPrimitive = Objects.isNull(componetType) ? false : componetType.isPrimitive();
			for (final T item : array) {
				if (item == value) {
					result = true;
					break;
				} else if (!isPrimitive && null != value && value.equals(item)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * 数组是否为空
	 * 
	 */
	@SafeVarargs
	public static <T> boolean isEmpty(final T... array) {
		return array == null || array.length <= 0;
	}

	/**
	 * 往数组中添加一个元素
	 */
	public static <T> T[] append(final T[] buffer, final T newElement) {
		final T[] array = resize(buffer, buffer.length + 1, newElement.getClass());
		array[buffer.length] = newElement;
		return array;
	}

	/**
	 * 过滤
	 */
	public static <T> T[] filter(final T[] array, final Filter<T> filter) {
		Assert.notEmpty(array, "filter array failure,the array is empty");
		final List<T> list = new ArrayList<T>();
		for (final T instance : array) {
			if (filter.accept(instance)) {
				list.add(instance);
			}
		}
		return list.toArray(Arrays.copyOf(array, list.size()));
	}

}
