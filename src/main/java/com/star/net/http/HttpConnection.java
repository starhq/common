package com.star.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.star.collection.ArrayUtil;
import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.net.URLUtil;
import com.star.net.http.ssl.DefaultTrustManager;
import com.star.net.http.ssl.TrustAnyHostnameVerifier;
import com.star.regex.Validator;
import com.star.string.StringUtil;

/**
 * http连接对象
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public class HttpConnection {

	/**
	 * 链接
	 */
	private URL url;

	/**
	 * http方法
	 */
	private transient HttpMethod httpMethod;
	/**
	 * 链接的连接对象
	 */
	private final transient HttpURLConnection conn;

	/**
	 * https用
	 */
	private transient HostnameVerifier hostnameVerifier;
	/**
	 * https用
	 */
	private transient TrustManager[] trustManagers;

	/**
	 * 构造方法
	 */
	public HttpConnection(final String urlStr, final HttpMethod httpMethod, final int timeout) {
		Assert.notNull(urlStr, "get httpeconnection failue,the url is blank");
		Assert.isTrue(!Validator.isUrl(urlStr), "get httpeconnection failue,the url is invalid");
		this.url = URLUtil.url(urlStr);
		this.httpMethod = Objects.isNull(httpMethod) ? HttpMethod.GET : httpMethod;
		this.conn = URLUtil.isHttps(urlStr) ? openHttps() : openHttp();
		if (timeout > 0) {
			setTimeout(timeout, 0);
		}
	}

	/**
	 * 
	 * 初始化连接相关信息
	 * 
	 * 
	 */
	public HttpConnection initconn() {
		Assert.notNull(conn, "init http connection failure,the connection hasn't create");
		try {
			this.conn.setRequestMethod(httpMethod.toString());
		} catch (ProtocolException e) {
			throw new ToolException(StringUtil.format("set http method failue,the reason is: {}", e.getMessage()), e);
		}
		this.conn.setDoInput(true);
		if (this.httpMethod.equals(HttpMethod.POST)) {
			this.conn.setDoOutput(true);
			this.conn.setUseCaches(false);
		}

		header(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", true);
		header(HttpHeader.ACCEPT_ENCODING, "gzip", true);
		header(HttpHeader.CONTENT_TYPE, "application/x-www-form-urlencoded", true);
		header(HttpHeader.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0 starhq",
				true);

		setCookie(CookiePool.get(this.url.getHost()));

		return this;
	}

	/**
	 * 
	 * 获取请求方法,GET/POST
	 */
	public HttpMethod getMethod() {
		return httpMethod;
	}

	/**
	 * 
	 * 设置请求方法
	 */
	public void setMethod(final HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * 
	 * 设置域名验证器<br>
	 */
	public void setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	/**
	 * 
	 * 设置信任信息
	 * 
	 */
	public void setTrustManagers(final TrustManager... trustManagers) {
		if (!ArrayUtil.isEmpty(trustManagers)) {
			this.trustManagers = trustManagers;
		}
	}

	/**
	 * 
	 * 获取请求URL
	 * 
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * 
	 * 设置请求URL
	 * 
	 */
	public void setUrl(final URL url) {
		this.url = url;
	}

	/**
	 * 
	 * 获取HttpURLConnection对象
	 * 
	 */
	public HttpURLConnection getHttpURLConnection() {
		return conn;
	}

	/**
	 * 
	 * 设置请求头
	 * 
	 */
	public HttpConnection header(final String header, final String value, final boolean isOverride) {
		if (!Objects.isNull(conn)) {
			if (isOverride) {
				this.conn.setRequestProperty(header, value);
			} else {
				this.conn.addRequestProperty(header, value);
			}
		}

		return this;
	}

	/**
	 * 设置请求头
	 */
	public HttpConnection header(final HttpHeader header, final String value, final boolean isOverride) {
		return header(header.toString(), value, isOverride);
	}

	/**
	 * 
	 * 设置请求头<br>
	 * 
	 */
	public HttpConnection header(final Map<String, List<String>> headers, final boolean isOverride) {
		if (!CollectionUtil.isEmpty(headers)) {
			for (final Entry<String, List<String>> entry : headers.entrySet()) {
				final String name = entry.getKey();
				for (final String value : entry.getValue()) {
					this.header(name, StringUtil.isBlank(value) ? "" : value, isOverride);
				}
			}
		}
		return this;
	}

	/**
	 * 获取Http请求头
	 */
	public String header(final HttpHeader header) {
		return this.conn.getHeaderField(header.toString());
	}

	/**
	 * 
	 * 获取所有Http请求头
	 * 
	 */
	public Map<String, List<String>> headers() {
		return this.conn.getHeaderFields();
	}

	/**
	 * 
	 * 关闭缓存
	 * 
	 */
	public HttpConnection disableCache() {
		this.conn.setUseCaches(false);
		return this;
	}

	/**
	 * 
	 * 设置连接超时
	 * 
	 * type -1 链接超时 1 读取超市 0 两样都设
	 */
	public HttpConnection setTimeout(final int timeout, final int type) {
		if (timeout > 0 && null != this.conn) {
			switch (type) {
			case -1:
				this.conn.setConnectTimeout(timeout);
				break;
			case 1:
				this.conn.setReadTimeout(timeout);
				break;
			default:
				this.conn.setConnectTimeout(timeout);
				this.conn.setReadTimeout(timeout);
				break;
			}
		}

		return this;
	}

	/**
	 * 
	 * 设置Cookie
	 * 
	 */
	public HttpConnection setCookie(final String cookie) {
		if (!Objects.isNull(cookie)) {
			header(HttpHeader.COOKIE, cookie, true);
		}
		return this;
	}

	/**
	 * 
	 * 采用流方式上传数据，无需本地缓存数据。<br>
	 * 
	 */
	public HttpConnection setChunkedStreamingMode(final int blockSize) {
		conn.setChunkedStreamingMode(blockSize);
		return this;
	}

	/**
	 * 
	 * 连接
	 * 
	 * 
	 * 
	 * @throws IOException
	 * 
	 */
	public HttpConnection connect() throws IOException {
		if (!Objects.isNull(this.conn)) {
			this.conn.connect();
		}
		return this;
	}

	/**
	 * 
	 * 断开连接
	 * 
	 */
	public HttpConnection disconnect() {
		if (!Objects.isNull(this.conn)) {
			this.conn.disconnect();
		}
		return this;
	}

	/**
	 * 获得输入流
	 */
	public InputStream getInputStream() {
		final String setCookie = header(HttpHeader.SET_COOKIE);
		if (!StringUtil.isBlank(setCookie)) {
			CookiePool.put(url.getHost(), setCookie);
		}
		try {
			return Objects.isNull(this.conn) ? null : this.conn.getInputStream();
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("从url链接中获取输入流失败: {}", e.getMessage()), e);
		}
	}

	/**
	 * 获得错误内容的出入流
	 */
	public InputStream getErrorStream() {
		return Objects.isNull(this.conn) ? null : this.conn.getErrorStream();
	}

	/**
	 * 获得错误内容的出出流
	 */
	public OutputStream getOutputStream() {
		try {
			return Objects.isNull(this.conn) ? null : this.conn.getOutputStream();
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get outputstream from httpconeection failue,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 
	 * 获取响应码
	 * 
	 * 
	 */
	public int responseCode() {
		try {
			return Objects.isNull(this.conn) ? 0 : this.conn.getResponseCode();
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get response code from connction failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 
	 * 获得字符集编码
	 * 
	 */
	public String charset() {
		return HttpUtil.getCharset(conn);
	}

	/**
	 * 初始化http请求
	 */
	private HttpURLConnection openHttp() {
		try {
			return (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("open http url connection failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 初始化https请求
	 */
	private HttpsURLConnection openHttps() {
		try {
			final HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();

			httpsConnection.setHostnameVerifier(
					null == this.hostnameVerifier ? new TrustAnyHostnameVerifier() : this.hostnameVerifier);

			final SSLContext sslContext = SSLContext.getInstance("TLS");
			final TrustManager[] trustManagers = this.trustManagers == null || this.trustManagers.length == 0
					? new TrustManager[] { new DefaultTrustManager() } : this.trustManagers;
			sslContext.init(null, trustManagers, new SecureRandom());
			httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());

			return httpsConnection;
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			throw new ToolException(
					StringUtil.format("open https url connection failure,the reason is: {}", e.getMessage()), e);
		}
	}

}
