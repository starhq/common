package com.star.security;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.star.io.CharsetUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * base64 加解密
 * 
 * @author starhq
 *
 */
public final class Base64Util {

	private Base64Util() {
	}

	/**
	 * base64加密
	 * 
	 */
	public static String base64Encode(final String value, final String charset) {
		Assert.notBlank(value, "use base64 to encode string failure,the string is blank");
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(StringUtil.string2Byte(value, CharsetUtil.charset(charset)));
	}

	/**
	 * base64解密
	 * 
	 */
	public static String base64Decode(final String value, final String charset) {
		Assert.notBlank(value, "use base64 to decode string failure,the string is blank");
		Decoder decoder = Base64.getDecoder();
		return StringUtil.byte2String(decoder.decode(value), CharsetUtil.charset(charset));
	}

}
