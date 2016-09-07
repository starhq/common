package com.star.net.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.star.collection.CollectionUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * http基类
 * 
 * @author http://git.oschina.net/loolly/hutool
 */
public abstract class AbstractHttpBase<T> {

	/** 存储头信息 */
	protected transient Map<String, List<String>> headers = new ConcurrentHashMap<>();

	/** http版本 */
	protected String httpVersion = HttpVersion.HTTP_1_1.toString();

	/** 字符集 */
	protected String charset = "UTF-8";

	/** 请求体 */
	protected transient String body;

	/** http版本常量 */
	enum HttpVersion {
		HTTP_1_0("HTTP/1.0"), HTTP_1_1("HTTP/1.1");

		/** http版本字符串 */
		private String value;

		private HttpVersion(final String value) {
			this.value = value;
		}

		/**
		 * 这都要注释啊，满刚的
		 */
		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * 空构造方法
	 */
	protected AbstractHttpBase() {
		// 空的，啥都不用做
	}

	/**
	 * 根据name获得头信息
	 */
	public String getHeader(final HttpHeader name) {
		Assert.notNull(name, "get header by name failure,the name is null");
		final List<String> values = headers.get(name.toString());
		return values.isEmpty() ? "" : values.get(0);
	}

	/**
	 * 删除头信息
	 */
	public void removeHeader(final String name) {
		if (!StringUtil.isBlank(name)) {
			headers.remove(name);
		}
	}

	/**
	 * 设置头信息
	 */
	@SuppressWarnings("unchecked")
	public T setHeader(final String name, final String value, final boolean isOverride) {
		if (null != name && null != value) {
			final List<String> values = headers.get(name);
			if (isOverride || CollectionUtil.isEmpty(values)) {
				final List<String> valueList = new ArrayList<>(1);
				valueList.add(value);
				headers.put(name, valueList);
			} else {
				values.add(value);
			}
		}
		return (T) this;
	}

	/**
	 * 设置请求头
	 */
	public T setHeader(final HttpHeader name, final String value, final boolean isOverride) {
		return setHeader(name.toString(), value, isOverride);
	}

	/**
	 * 获取头信息
	 */
	public Map<String, List<String>> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	/**
	 * 返回http版本
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * 设置httpversion
	 */
	@SuppressWarnings("unchecked")
	public T setHttpVersion(final HttpVersion httpVersion) {
		this.httpVersion = httpVersion.toString();
		return (T) this;
	}

	/**
	 * 
	 * 返回字符集
	 * 
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * 
	 * 设置字符集
	 */
	@SuppressWarnings("unchecked")
	public T setCharset(final String charset) {
		this.charset = charset;
		return (T) this;
	}

	/**
	 * 这他喵的都要注释
	 */
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(39);
		stringBuilder.append("Request Headers: ").append(StringUtil.CRLF);
		for (final Entry<String, List<String>> entry : this.headers.entrySet()) {
			stringBuilder.append("    ").append(entry).append(StringUtil.CRLF);
		}

		stringBuilder.append("Request Body: ").append(StringUtil.CRLF).append("    ").append(this.body)
				.append(StringUtil.CRLF);

		return stringBuilder.toString();
	}

}
