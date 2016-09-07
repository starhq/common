package com.star.io.serializer;

/**
 * 序列化接口
 */
public interface Serializer {
	/**
	 * 用的哪种序列化方式
	 */
	String name();

	/**
	 * 序列化
	 */
	byte[] serialize(Object obj);

	/**
	 * 反序列化
	 */
	Object deserialize(byte[] bytes);
}
