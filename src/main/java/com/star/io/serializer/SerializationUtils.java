package com.star.io.serializer;

import com.star.clazz.ClassUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 序列话工厂
 * 
 * @author starhq
 *
 */
public final class SerializationUtils {

	/**
	 * 类名占位符
	 */
	private final static String CLASSNAME = "com.star.io.serializer.{}Serializer";

	private SerializationUtils() {
	}

	/**
	 * 获得序列化产品
	 */
	public static Serializer productSerializer(final String serialName) {
		Assert.isTrue(!StringUtil.isBlank(serialName), "get serializer by factory failue,the input serialName is null");
		final String clazz = StringUtil.format(CLASSNAME, serialName);
		return ClassUtil.newInstance(ClassUtil.loadClass(clazz));
	}

	/**
	 * 序列化
	 */
	// public static byte[] serialize(final Object obj) {
	// return serializer.serialize(obj);
	// }

	/**
	 * 反序列化
	 */
	// public static Object deserialize(final byte[] bytes) {
	// return serializer.deserialize(bytes);
	// }

}
