package com.star.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.IoUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

/**
 * fst实现序列化
 * 
 * @author starhq
 *
 */
public class FSTSerializer implements Serializer {

	/**
	 * fst
	 */
	@Override
	public String name() {
		return "fst";
	}

	/**
	 * 序列化对象
	 */
	@Override
	public byte[] serialize(final Object obj) {
		Assert.notNull(obj, "fst serialize obj failure,the input object is null");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			final FSTObjectOutput foo = new FSTObjectOutput(baos);
			foo.writeObject(obj);
			IoUtil.close(foo);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("fst serialize obj failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 反序列化对象
	 */
	@Override
	public Object deserialize(final byte[] bytes) {
		Assert.isTrue(!ArrayUtil.isEmpty(bytes), "fst deserialize obj failure,the input object is null");
		final byte[] copy = bytes.clone();
		final ByteArrayInputStream bais = new ByteArrayInputStream(copy);
		try {
			final FSTObjectInput foi = new FSTObjectInput(bais);
			IoUtil.close(foi);
			return foi.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new ToolException(StringUtil.format("fst deserialize obj failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

}
