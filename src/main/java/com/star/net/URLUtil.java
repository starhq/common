package com.star.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * url工具类
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public final class URLUtil {

	private URLUtil() {

	}

	/**
	 * 创建url
	 */
	public static URL url(final String url) {
		Assert.notBlank(url, "create url failue,the input url string is null");
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new ToolException(StringUtil.format("create url failure,the reason is :{}", e.getMessage()), e);
		}
	}

	/**
	 * 获得文件的url
	 */
	public static URL getURL(final File file) {
		Assert.notNull(file, "get file's url failue,the input file is null");
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new ToolException(StringUtil.format("get file's url failue,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 格式化url连接
	 */
	public static String formatUrl(final String url) {
		Assert.notBlank(url, "format url failue,the input url string is null");
		return url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url;
	}

	/**
	 * 补全相对路径
	 */
	public static String complateUrl(final String baseUrl, final String relativePath) {
		Assert.notNull(relativePath, "complate url failure,the input relativePath is null");
		final String fullUrl = formatUrl(baseUrl);

		final URL absoluteUrl = url(fullUrl);
		try {
			final URL parseUrl = new URL(absoluteUrl, relativePath);
			return parseUrl.toString();
		} catch (MalformedURLException e) {
			throw new ToolException(StringUtil.format("complate url failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 编码url
	 */
	public static String encode(final String url, final String charset) {
		try {
			return URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException e) {
			throw new ToolException(StringUtil.format("encode url failue,the system doesn't support {}", charset), e);
		}
	}

	/**
	 * 解码url
	 */
	public static String decode(final String url, final String charset) {
		try {
			return URLDecoder.decode(url, charset);
		} catch (UnsupportedEncodingException e) {
			throw new ToolException(StringUtil.format("encode url failue,the system doesn't support {}", charset), e);
		}
	}

	/**
	 * 获得path部分
	 */
	public static String getPath(final String uriStr) {
		Assert.notNull(uriStr, "get uri's path failure,the input string is null");
		try {
			final URI uri = new URI(uriStr);
			return Objects.isNull(uri) ? "" : uri.getPath();
		} catch (URISyntaxException e) {
			throw new ToolException(StringUtil.format("get uri's path failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 
	 * 检测是否https
	 * 
	 */
	public static boolean isHttps(final String url) {
		return url.toLowerCase().startsWith("https");
	}
}
