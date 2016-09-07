package com.star.object;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import com.star.clazz.ClassUtil;
import com.star.lang.Assert;
import com.star.reflect.MethodUtil;
import com.star.string.StringUtil;

/**
 * 对象工具类
 * 
 * @author starhq
 *
 */
public final class ObjectUtil {

	private ObjectUtil() {
	}

	/**
	 * 
	 * 克隆对象<br>
	 * 
	 */
	public static <T extends Cloneable> T clone(final T obj) {
		Assert.notNull(obj, "clone object failure,the obj is null");
		final Method cloneMethod = MethodUtil.findDeclaredMethod(obj.getClass(), "clone");
		return MethodUtil.invoke(obj, cloneMethod);
	}

	/**
	 * 克隆集合
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Collection<U>, U extends Cloneable> T cloneCollection(final T orig) {
		return (T) cloneCollection(orig.getClass(), orig);
	}

	/**
	 * 克隆集合
	 */
	public static <I extends Collection<U>, O extends Collection<U>, U extends Cloneable> O cloneCollection(
			final Class<O> type, final I orig) {
		O obj = null;
		if (!Objects.isNull(orig)) {
			obj = ClassUtil.newInstance(type);
			for (final U element : orig) {
				obj.add(clone(element));
			}
		}
		return obj;
	}

	/**
	 * 检查对象好似否为数组
	 */
	public static boolean isArray(final Object obj) {
		Assert.notNull(obj, "Determine whether the obj is array failure,the obj is null");
		return obj.getClass().isArray();
	}

	/**
	 * 对象转字符串
	 */
	public static String object2String(final Object object, final Charset charset) {
		Assert.notNull(object, "convert object to string failue,the input object is null");
		String str;

		if (object instanceof byte[] || object instanceof Byte[]) {
			str = StringUtil.byte2String((byte[]) object, charset);
		} else if (object instanceof ByteBuffer) {
			str = StringUtil.byteBuffer2String((ByteBuffer) object, charset);
		} else if (isArray(object)) {
			str = Arrays.toString((Object[]) object);
		} else {
			str = object.toString();
		}

		return str;

	}

}
