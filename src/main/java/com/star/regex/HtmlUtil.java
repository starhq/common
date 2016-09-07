package com.star.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 处理html的工具类
 * 
 * @author starhq
 *
 */
public final class HtmlUtil {

	/**
	 * html正则
	 */
	public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
	/**
	 * js的正则
	 */
	public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

	/**
	 * html标签的正则
	 */
	private final static String HTML = "<{}[^<>]*?\\s{}=['\"]?(.*?)['\"]?\\s.*?>";

	/**
	 * html转义字符
	 */
	private static final char[][] TEXT = new char[64][];

	static {
		for (int i = 0; i < 64; i++) {
			TEXT[i] = new char[] { (char) i };
		}

		// special HTML characters

		TEXT['\''] = "&#039;".toCharArray(); // 单引号 ('&apos;' doesn't work - it
												// is not by the w3 specs)

		TEXT['"'] = StringUtil.HTML_QUOTE.toCharArray(); // 双引号

		TEXT['&'] = StringUtil.HTML_AMP.toCharArray(); // &符

		TEXT['<'] = StringUtil.HTML_LT.toCharArray(); // 小于号

		TEXT['>'] = StringUtil.HTML_GT.toCharArray(); // 大于号

	}

	private HtmlUtil() {
	}

	/**
	 * 还原被html转移的字符串
	 */
	public static String restoreEscaped(final String htmlStr) {
		Assert.notBlank(htmlStr, "restore html escape string failure,the input string is null");
		return htmlStr.replace("&#39;", "'").replace(StringUtil.HTML_LT, "<").replace(StringUtil.HTML_GT, ">")
				.replace(StringUtil.HTML_AMP, "&").replace(StringUtil.HTML_QUOTE, "\"")
				.replace(StringUtil.HTML_NBSP, " ");
	}

	/**
	 * 转义文本中的HTML字符为安全的字符
	 * 
	 * &amp; with &amp;amp;(有点变扭)
	 */
	public static String encode(final String text) {
		return encode(text, TEXT);
	}

	/**
	 * 清除所有html标签
	 */
	public static String cleanHtmlTag(final String content) {
		Assert.notNull(content, "clear html tag failure,the input string is null");
		return content.replaceAll(RE_HTML_MARK, "");
	}

	/**
	 * 清除指定html标签
	 */
	public static String removeHtmlTag(final String content, final boolean withTagContent, final String... tagNames) {
		Assert.notNull(content, "clear html tag failure,the input string is null");
		String regex1 = null;
		String regex2 = null;
		String result = content;
		for (final String tagName : tagNames) {
			if (StringUtil.isBlank(tagName)) {
				continue;
			}
			regex1 = StringUtil.format("(?i)<{}\\s?[^>]*?/>", tagName);
			if (withTagContent) {
				regex2 = StringUtil.format("(?i)(?s)<{}\\s*?[^>]*?>.*?</{}>", tagName, tagName);
			} else {
				regex2 = StringUtil.format("(?i)<{}\\s*?[^>]*?>|</{}>", tagName, tagName);
			}
			result = content.replaceAll(regex1, StringUtil.EMPTY).replaceAll(regex2, StringUtil.EMPTY);
		}
		return result;
	}

	/**
	 * 
	 * 文本转html
	 * 
	 */
	public static String txt2htm(final String txt) {
		String result = txt;
		if (!StringUtil.isBlank(txt)) {
			StringBuilder sb = new StringBuilder((int) (txt.length() * 1.2));
			char c;
			boolean doub = false;
			for (int i = 0; i < txt.length(); i++) {
				c = txt.charAt(i);
				if (c == ' ') {
					if (doub) {
						sb.append(' ');
						doub = false;
					} else {
						sb.append("&nbsp;");
						doub = true;
					}
				} else {
					doub = false;
					switch (c) {
					case '&':
						sb.append("&amp;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					case '"':
						sb.append("&quot;");
						break;
					case '\n':
						sb.append("<br/>");
						break;
					default:
						sb.append(c);
						break;
					}
				}
			}
			result = sb.toString();
		}
		return result;
	}

	/**
	 * 去除HTML标签中的属性
	 */
	public static String removeHtmlAttr(final String content, final String... attrs) {
		Assert.notNull(content, "remove html tag's attribute failure,the input sting is null");
		String result = content;
		String regex;
		for (final String attr : attrs) {
			regex = StringUtil.format("(?i)\\s*{}=([\"']).*?\\1", attr);
			result = result.replaceAll(regex, StringUtil.EMPTY);
		}
		return result;
	}

	/**
	 * 去掉指定标签中的所有属性
	 */
	public static String removeAllHtmlAttr(final String content, final String... tagNames) {
		Assert.notNull(content, "remove html tag's attribute failure,the input sting is null");
		String regex = null;
		String result = content;
		for (final String tagName : tagNames) {
			regex = StringUtil.format("(?i)<{}[^>]*?>", tagName);
			result = result.replaceAll(regex, StringUtil.format("<{}>", tagName));
		}
		return result;
	}

	/**
	 * 编码文本
	 */
	private static String encode(final String text, final char[][] array) {
		StringBuilder builder;
		if (StringUtil.isBlank(text)) {
			builder = new StringBuilder();
		} else {
			final int len = text.length();
			builder = new StringBuilder(len + (len >> 2));
			for (int i = 0; i < len; i++) {
				final char current = text.charAt(i);
				if (current < 64) {
					builder.append(array[current]);
				} else {
					builder.append(current);
				}
			}
		}
		return builder.toString();
	}

	/**
	 * 获取指定HTML标签的指定属性的值
	 */
	public static List<String> match(final String source, final String element, final String attr) {
		final List<String> result = new ArrayList<String>();
		final String reg = StringUtil.format(HTML, element, attr);
		final Matcher matcher = Pattern.compile(reg).matcher(source);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}
}
