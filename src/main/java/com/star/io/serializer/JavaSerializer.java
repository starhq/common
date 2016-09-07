package com.star.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.FastByteArrayOutputStream;
import com.star.io.IoUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 序列化java实现
 * 
 * @author starhq
 *
 */
public class JavaSerializer implements Serializer {

	/**
	 * java
	 */
	@Override
	public String name() {
		return "java";
	}

	/**
	 * 序列化对象
	 */
	@Override
	public byte[] serialize(final Object obj) {
		Assert.notNull(obj, "jdk serialize obj failure,the input object is null");
		final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(byteOut);
			oos.writeObject(obj);
			oos.flush();
			return byteOut.toByteArray();
		} catch (Exception e) {
			throw new ToolException(StringUtil.format("jdk serialize obj failure,the reason is: {}", e.getMessage()),
					e);
		} finally {
			IoUtil.close(oos);
		}
	}

	/**
	 * 反序列化对象
	 */
	@Override
	public Object deserialize(final byte[] bytes) {
		Assert.isTrue(!ArrayUtil.isEmpty(bytes), "jdk deserialize obj failure,the input object is null");
		final byte[] copy = bytes.clone();
		final ByteArrayInputStream bais = new ByteArrayInputStream(copy);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new ToolException(StringUtil.format("jdk deserialize obj failure,the reason is: {}", e.getMessage()),
					e);
		} finally {
			IoUtil.close(ois);
		}
	}

}
