package com.star.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.star.string.StringUtil;

/**
 * 字符集工具类
 * 
 * @author starhq
 *
 */
public final class CharsetUtil {

	/** ISO-8859-1 */
	public static final String ISO_8859_1 = "ISO-8859-1";
	/** UTF-8 */
	public static final String UTF_8 = "UTF-8";
	/** GBK */
	public static final String GBK = "GBK";

	/** ISO-8859-1 */
	public static final Charset CHARSET_8859 = Charset.forName(ISO_8859_1);
	/** UTF-8 */
	public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);
	/** GBK */
	public static final Charset CHARSET_GBK = Charset.forName(GBK);
	/** default */
	public static final Charset DEFAULT = Charset.defaultCharset();

	private CharsetUtil() {
	}

	/**
	 * 转换为Charset对象
	 */
	public static Charset charset(final String charset) {
		return StringUtil.isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset);
	}

	/**
	 * 
	 * 转换字符串的字符集编码
	 * 
	 * 默认8859-1到utf-8
	 * 
	 */
	public static String convert(final String source, final Charset srcCharset, final Charset destCharset) {

		final Charset src = Objects.isNull(srcCharset) ? StandardCharsets.ISO_8859_1 : srcCharset;

		final Charset dest = Objects.isNull(destCharset) ? StandardCharsets.UTF_8 : destCharset;

		return StringUtil.isBlank(source) || src.equals(dest) ? source
				: StringUtil.byte2String(source.getBytes(src), dest);
	}

	/**
	 * 系统字符集编码
	 */
	public static String systemCharset() {
		return Charset.defaultCharset().name();
	}

}
