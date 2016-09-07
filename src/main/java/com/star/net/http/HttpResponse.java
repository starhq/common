package com.star.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import com.star.exception.pojo.ToolException;
import com.star.io.CharsetUtil;
import com.star.io.FastByteArrayOutputStream;
import com.star.io.IoUtil;
import com.star.string.StringUtil;

/**
 * http相应类
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public class HttpResponse extends AbstractHttpBase<HttpResponse> {

	/**
	 * 读取服务器返回的流保存至内存
	 */
	private transient FastByteArrayOutputStream output;

	/**
	 * 响应码
	 */
	private transient int status;

	/**
	 * 读取http连接中的响应内容
	 */
	public static HttpResponse readResponse(final HttpConnection httpConnection) {
		final HttpResponse httpResponse = new HttpResponse();

		httpResponse.status = httpConnection.responseCode();
		httpResponse.headers = httpConnection.headers();
		httpResponse.charset = httpConnection.charset();

		InputStream input;
		if (httpResponse.status < HttpURLConnection.HTTP_BAD_REQUEST) {
			input = httpConnection.getInputStream();
		} else {
			input = httpConnection.getErrorStream();
		}

		httpResponse.readBody(input);

		return httpResponse;
	}

	/**
	 * 获得响应码
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 获取内容编码
	 */
	public String contentEncoding() {
		return getHeader(HttpHeader.CONTENT_ENCODING);
	}

	/**
	 * 获得相应的字节数组
	 */
	public byte[] bodyBytes() {
		byte[] bytes;
		if (null == this.output) {
			bytes = new byte[0];
		} else {
			bytes = this.output.toByteArray();
		}
		return bytes;
	}

	/**
	 * 获得响应体
	 * 
	 * @return
	 */
	public String getBody() {
		return IoUtil.read(bodyStream()).toString(CharsetUtil.charset(charset));
	}

	/**
	 * 获得服务区响应流
	 */
	public InputStream bodyStream() {
		return new ByteArrayInputStream(this.output.toByteArray());
	}

	/**
	 * 这都要注解嘛
	 */
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(50);
		stringBuilder.append("Request Headers: ").append(StringUtil.CRLF);
		for (final Entry<String, List<String>> entry : this.headers.entrySet()) {
			stringBuilder.append("    ").append(entry).append(StringUtil.CRLF);
		}

		stringBuilder.append("Request Body: ").append(StringUtil.CRLF).append("    ").append(this.getBody())
				.append(StringUtil.CRLF);

		return stringBuilder.toString();
	}

	/**
	 * 
	 * 是否为gzip压缩过的内容
	 * 
	 */
	public boolean isGzip() {
		final String contentEncoding = contentEncoding();
		return contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip");
	}

	private void readBody(final InputStream inputStream) {
		InputStream input;
		try {
			input = isGzip() ? new GZIPInputStream(inputStream) : inputStream;
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("read response's stream failure,the reason is: {}", e.getMessage()), e);
		}

		final String length = getHeader(HttpHeader.CONTENT_LENGTH);
		final int contentLength = StringUtil.isBlank(length) ? 0 : Integer.valueOf(length);
		this.output = contentLength > 0 ? new FastByteArrayOutputStream(contentLength)
				: new FastByteArrayOutputStream();
		IoUtil.copy(input, output, 0);
	}
}
