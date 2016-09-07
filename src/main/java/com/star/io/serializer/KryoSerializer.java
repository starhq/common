package com.star.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.star.collection.ArrayUtil;
import com.star.io.IoUtil;
import com.star.lang.Assert;

/**
 * kyro序列化实现
 * 
 * @author starhq
 *
 */
public class KryoSerializer implements Serializer {

	/**
	 * kryo全局话
	 */
	private transient final Kryo kryo = new Kryo();

	/**
	 * kyro
	 */
	@Override
	public String name() {
		return "kyro";
	}

	/**
	 * 序列化对象
	 */
	@Override
	public byte[] serialize(final Object obj) {
		Assert.notNull(obj, "kryo serialize obj failure,the input object is null");
		Output output = null;
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			output = new Output(baos);
			kryo.writeClassAndObject(output, obj);
			output.flush();
			return baos.toByteArray();
		} finally {
			IoUtil.close(output);
		}
	}

	/**
	 * 反序列化对象
	 */
	@Override
	public Object deserialize(final byte[] bytes) {
		Assert.isTrue(!ArrayUtil.isEmpty(bytes), "kryo deserialize obj failure,the input object is null");
		Input input = null;
		final byte[] copy = bytes.clone();
		try {
			final ByteArrayInputStream bais = new ByteArrayInputStream(copy);
			input = new Input(bais);
			return kryo.readClassAndObject(input);
		} finally {
			IoUtil.close(input);
		}
	}

}
