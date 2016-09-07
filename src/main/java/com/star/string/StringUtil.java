package com.star.string;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.star.collection.ArrayUtil;
import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.CharsetUtil;
import com.star.lang.Assert;

/**
 * 字符串处理方法
 * 
 * @author starhq
 *
 */
public final class StringUtil {

	/** 空格 */
	public static final String SPACE = " ";
	/** 点 */
	public static final String DOT = ".";
	/** 斜杠 */
	public static final String SLASH = "/";
	/** 反斜杠 */
	public static final String BACKSLASH = "\\";
	/** 空斜杠 */
	public static final String EMPTY = "";
	/** 换行 */
	public static final String NEWLINE = "\n";
	/** 下划线 */
	public static final String UNDERLINE = "_";
	/** 逗号 */
	public static final String COMMA = ",";

	/** 空格转移 */
	public static final String HTML_NBSP = "&nbsp;";
	/** &转义 */
	public static final String HTML_AMP = "&amp;";
	/** "转义 */
	public static final String HTML_QUOTE = "&quot;";
	/** <转义 */
	public static final String HTML_LT = "&lt;";
	/** >转义 */
	public static final String HTML_GT = "&gt;";
	/** 空json */
	public static final String EMPTY_JSON = "{}";
	/** 回车换行 */
	public static final String CRLF = "\r\n";
	/** 类字符串常量 */
	public static final String CLASS = "class";
	/** 接口字符串常量 */
	public static final String INTERFACE = "interface";

	private StringUtil() {
	}

	/**
	 * 字符串是否为空白
	 */
	public static boolean isBlank(final String str) {
		boolean result = true;
		if (str == null || str.length() == 0) {
			result = true;
		} else {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isWhitespace(str.charAt(i))) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 字符串是否为空
	 */
	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 大小写首字母
	 */
	public static String upperOrLowerFirst(final String str, final boolean upper) {
		Assert.notBlank(str, "change first character to upper or lower failure,the input string is blank");
		return upper ? Character.toUpperCase(str.charAt(0)) + str.substring(1)
				: Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	/**
	 * 去掉指定前缀
	 */
	public static String removePrefix(final String str, final String prefix) {
		Assert.notBlank(str, "remove prefix failure,the  string is blank");
		Assert.notBlank(prefix, "remove prefix failure,the prefix is blank");
		String result;
		if (str.startsWith(prefix)) {
			result = str.substring(prefix.length());
		} else {
			throw new ToolException(
					format("remove prefix failure,the input string {} isn't start with {}", str, prefix));
		}
		return result;
	}

	/**
	 * 去掉指定后缀
	 */
	public static String removeSuffix(final String str, final String suffix) {
		Assert.notBlank(str, "remove suffix failure,the  string is blank");
		Assert.notBlank(suffix, "remove suffix failure,the suffix is blank");
		String result;
		if (str.endsWith(suffix)) {
			result = str.substring(0, str.length() - suffix.length());
		} else {
			throw new ToolException(
					format("remove suffix failure,the input string {} isn't ends with {}", str, suffix));
		}
		return result;
	}

	/**
	 * 切分字符串
	 */
	public static List<String> split(final String str, final char separator, final int limit) {
		Assert.notBlank(str, "split string into list failue,the input string is null");
		final List<String> list = CollectionUtil.getList(limit == 0 ? 16 : limit);
		if (limit == 1) {
			list.add(str);
		} else {

			boolean isNotEnd = true; // 未结束切分的标志

			final StringBuilder builder = new StringBuilder(str.length());
			for (int i = 0; i < str.length(); i++) {
				final char current = str.charAt(i);
				if (isNotEnd && current == separator) {
					list.add(builder.toString());
					// 清空StringBuilder

					builder.delete(0, builder.length());

					// 当达到切分上限-1的量时，将所剩字符全部作为最后一个串

					if (limit != 0 && list.size() == limit - 1) {
						isNotEnd = false;
					}
				} else {
					builder.append(current);
				}
			}
			list.add(builder.toString());// 加入尾串
		}
		return list;
	}

	/**
	 * 切分字符串,这个比原生的快
	 */
	public static String[] split(final String str, final String delimiter) {
		Assert.notBlank(str, "split spring failue,the input string is nul");
		final int dellen = delimiter.length();

		final int maxparts = str.length() / dellen + 2;

		int[] positions = new int[maxparts];

		int start = 0;
		int end = 0;
		int count = 0;
		positions[0] = -dellen;
		while ((start = str.indexOf(delimiter, end)) != -1) {
			count++;
			positions[count] = start;
			end = start + dellen;
		}
		count++;
		positions[count] = str.length();

		String[] result = new String[count];

		for (start = 0; start < count; start++) {
			result[start] = str.substring(positions[start] + dellen, positions[start + 1]);
		}
		return result;
	}

	/**
	 * 切分字符串
	 */
	public static String sub(final String string, final int fromIndex, final int toIndex) {
		int start = fromIndex < 0 ? string.length() + fromIndex : fromIndex;
		int end = toIndex < 0 ? string.length() + toIndex : toIndex == 0 && fromIndex < 0 ? string.length() : toIndex;

		if (end < start) {
			start = start ^ end;
			end = start ^ end;
			start = start ^ end;
		}

		// 不知道为啥，这个慢
		// char[] strArray = string.toCharArray();
		// char[] newStrArray = Arrays.copyOfRange(strArray, fromIndex,
		// toIndex);
		// return new String(newStrArray);

		return string.substring(start, end);
	}

	/**
	 * 从左切分
	 */
	public static String subLeft(final String string, final int toIndex) {
		return sub(string, 0, toIndex);
	}

	/**
	 * 从右切分
	 */
	public static String subRight(final String string, final int fromIndex) {
		return sub(string, fromIndex, string.length());
	}

	/**
	 * string转byte
	 */
	public static byte[] string2Byte(final String str, final Charset charset) {
		Assert.notNull(str, "string to bytes failure,the input string is null");
		return Objects.isNull(charset) ? str.getBytes() : str.getBytes(charset);
	}

	/**
	 * byte转string
	 */
	public static String byte2String(final byte[] data, final Charset charset) {
		Assert.isTrue(!ArrayUtil.isEmpty(data), "byte to string failue,the input byte array is null");
		return charset == null ? new String(data) : new String(data, charset);
	}

	/**
	 * bytebuffer转string
	 */
	public static String byteBuffer2String(final ByteBuffer data, final Charset charset) {
		Assert.notNull(data, "bytebuffer convert to string failue,the input bytebuffer is null");
		return (charset == null ? Charset.defaultCharset() : charset).decode(data).toString();
	}

	/**
	 * string转bytebuffer
	 */
	public static ByteBuffer string2ByteBuffer(final String data, final String charset) {
		return ByteBuffer.wrap(string2Byte(data, CharsetUtil.charset(charset)));
	}

	/**
	 * 格式化文本
	 */
	public static String format(final String template, final Object... values) {
		Assert.notBlank(template, "format string failue,the  string is null");
		Assert.notEmpty(values, "format string failue,the  values is null");
		final StringBuilder stringBuilder = new StringBuilder();
		final int length = template.length();

		int valueIndex = 0;
		char currentChar;
		for (int i = 0; i < length; i++) {
			if (valueIndex >= values.length) {
				stringBuilder.append(template.substring(i, length));
				break;
			}

			currentChar = template.charAt(i);
			if (currentChar == '{') {
				final char nextChar = template.charAt(++i);
				if (nextChar == '}') {
					stringBuilder.append(values[valueIndex++]);
				} else {
					stringBuilder.append('{').append(nextChar);
				}
			} else {
				stringBuilder.append(currentChar);
			}

		}

		return stringBuilder.toString();
	}

	/**
	 * 
	 * 格式化文本
	 */
	public static String format(final String template, final Map<?, ?> map) {
		Assert.notBlank(template, "format string failue,the  string is null");
		Assert.notEmpty(map, "format string failue,the  map is null");
		String str = template;

		for (final Entry<?, ?> entry : map.entrySet()) {
			str = str.replace("{" + entry.getKey() + "}", entry.getValue().toString());
		}
		return str;
	}

	/**
	 * HelloWorld->hello_world
	 */
	public static String toUnderlineCase(final String camelCaseStr) {
		Assert.notNull(camelCaseStr,
				"camelCaseStr convert to lower case sting with under line failue,the input string is null");
		final int length = camelCaseStr.length();
		final StringBuilder builder = new StringBuilder();
		char cha;
		boolean isPreUpperCase = false;
		for (int i = 0; i < length; i++) {
			cha = camelCaseStr.charAt(i);
			boolean isNextUpperCase = true;
			if (i < (length - 1)) {
				isNextUpperCase = Character.isUpperCase(camelCaseStr.charAt(i + 1));
			}
			if (Character.isUpperCase(cha)) {
				if ((!isPreUpperCase || !isNextUpperCase) && i > 0) {
					builder.append(UNDERLINE);
				}
				isPreUpperCase = true;
			} else {
				isPreUpperCase = false;
			}
			builder.append(Character.toLowerCase(cha));
		}
		return builder.toString();
	}

	/**
	 * hello_world->HelloWorld
	 */
	public static String toCamelCase(final String name) {
		Assert.notNull(name, "string with underline convert to camelCaseStr failure,the input string is null");
		String result = name;
		if (result.contains(UNDERLINE)) {
			result = result.toLowerCase();

			final StringBuilder builder = new StringBuilder(name.length());
			boolean upperCase = false;
			char cha;
			for (int i = 0; i < name.length(); i++) {
				cha = name.charAt(i);

				if (cha == '_') {
					upperCase = true;
				} else if (upperCase) {
					builder.append(Character.toUpperCase(cha));
					upperCase = false;
				} else {
					builder.append(cha);
				}
			}
			result = builder.toString();
		}
		return result;
	}

}