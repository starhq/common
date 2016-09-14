package com.star.net.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.star.collection.ArrayUtil;
import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.CharsetUtil;
import com.star.io.IoUtil;
import com.star.lang.Assert;
import com.star.net.URLUtil;
import com.star.regex.RegexUtil;
import com.star.regex.Validator;
import com.star.string.StringUtil;

/**
 * 
 * Http请求工具类
 * 
 * @author http://git.oschina.net/loolly/hutool
 * 
 */
public final class HttpUtil {
	/**
	 * 字符集正则
	 */
	public final static Pattern CHARSET_PATTERN = Pattern.compile("charset=(.*?)\"");

	/**
	 * 构造方法
	 */
	private HttpUtil() {
		// 防止被初始化
	}

	/**
	 * 获取客户端ip
	 */
	public static String getClientIP(final HttpServletRequest request, final String... otherHeaderNames) {
		String[] headers = new String[] { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP" };
		if (otherHeaderNames != null && otherHeaderNames.length > 0) {
			headers = ArrayUtil.addAll(otherHeaderNames);
		}

		String result = "";
		for (final String header : headers) {
			String ipStr = request.getHeader(header);
			if (!isUnknow(ipStr)) {
				result = getMultistageReverseProxyIp(ipStr);
				break;
			}
		}

		return StringUtil.isBlank(result) ? getMultistageReverseProxyIp(request.getRemoteAddr()) : result;

	}

	/**
	 * 发送get或者delete请求
	 */
	public static String get(final String urlString, final String customCharset, final HttpMethod httpMethod) {
		Assert.notBlank(urlString, "send get or delete request failure,the input url is blank");
		Assert.notNull(httpMethod, "send get or delete request failure,the input http method is null");
		final HttpRequest httpRequest = HttpRequest.getRequest(urlString, httpMethod);
		if (!StringUtil.isBlank(customCharset)) {
			httpRequest.setCharset(customCharset);
		}
		return httpRequest.execute().getBody();
	}

	/**
	 * 发送post或者put请求
	 */
	public static String post(final String urlString, final String params, final HttpMethod httpMethod) {
		Assert.notBlank(urlString, "send post or put request failure,the input url is blank");
		Assert.notNull(httpMethod, "send post or put request failure,the input http method is null");
		return HttpRequest.getRequest(urlString, httpMethod).body(params).execute().getBody();
	}

	/**
	 * 发送post或者put请求
	 */
	public static String post(final String urlString, final Map<String, Object> paramMap, final HttpMethod httpMethod) {
		Assert.notBlank(urlString, "send post or put request failure,the input url is blank");
		Assert.notNull(httpMethod, "send post or put request failure,the input http method is null");
		return HttpRequest.getRequest(urlString, httpMethod).setForm(paramMap).execute().getBody();
	}

	/**
	 * 获得远程String
	 */
	public static String downloadString(final String url, final String customCharset) {
		Assert.notBlank(url, "read url's content failure,the input url is blank");
		InputStream input;
		try {
			input = URLUtil.url(url).openStream();
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("open stream from url failure,the reason is: {}", e.getMessage()),
					e);
		}
		try {
			return IoUtil.read(input).toString(CharsetUtil.charset(customCharset));
		} finally {
			IoUtil.close(input);
		}
	}

	/**
	 * 下载文件
	 */
	public static long downloadFile(final String url, final File file) {
		Assert.notBlank(url, "download file failure,the input url is blank");
		Assert.notNull(file, "download file failure,the file is null");
		InputStream input = null;
		OutputStream output = null;
		try {
			input = URLUtil.url(url).openStream();
			output = new FileOutputStream(file);
			return IoUtil.copy(input, output, 0);
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("download faile failure,the reason is: {}", e.getMessage()), e);
		} finally {
			IoUtil.close(input);
			IoUtil.close(output);
		}
	}

	/**
	 * 
	 * 将Map形式的Form表单数据转换为Url参数形式
	 * 
	 * 
	 */
	public static String toParams(final Map<String, Object> paramMap) {
		Assert.notNull(paramMap, "map convert to url parameter failure,the input map is null");
		return paramMap.isEmpty() ? "" : CollectionUtil.join(paramMap.entrySet(), '&');
	}

	/**
	 * 
	 * 将Map形式的Form表单数据转换为Url参数形式
	 * 
	 */
	public static String toParams(final Map<String, Object> paramMap, final String charset) {
		final Map<String, Object> maps = new ConcurrentHashMap<>(paramMap.size());
		for (final Entry<String, Object> entry : paramMap.entrySet()) {
			maps.put(URLUtil.encode(entry.getKey(), charset), URLUtil.encode(entry.getValue().toString(), charset));
		}
		return toParams(maps);
	}

	/**
	 * 将url参数解析为Map
	 */
	public static Map<String, List<String>> decodeParams(final String paramsStr, final String charset) {
		// 直接用分隔符截成n个数组来处理，逻辑简单点也能达到效果
		Map<String, List<String>> params;

		if (StringUtil.isBlank(paramsStr)) {
			params = Collections.emptyMap();
		} else {

			// 去掉Path部分

			final int pathEndPos = paramsStr.indexOf('?');

			String url = pathEndPos > 0 ? paramsStr.substring(pathEndPos + 1) : paramsStr;

			url = URLUtil.decode(url, charset);

			final String[] kvPairs = url.split("&");

			params = new LinkedHashMap<String, List<String>>(kvPairs.length);

			for (final String kv : kvPairs) {
				if (kv.indexOf('=') == -1) {
					addParam(params, kv, "");
				} else {
					final String[] temp = kv.split("=");
					addParam(params, temp[0], temp.length == 2 ? temp[1] : "");
				}

			}
		}

		return params;

		// 按字符串一个个解析
		// Map<String, List<String>> params;
		//
		// if (StringUtil.isBlank(paramsStr)) {
		// params = Collections.emptyMap();
		// } else {
		//
		// // 去掉Path部分
		//
		// final int pathEndPos = paramsStr.indexOf('?');
		//
		// String url = pathEndPos > 0 ? paramsStr.substring(pathEndPos + 1) :
		// paramsStr;
		//
		// url = URLUtil.decode(url, charset);
		//
		// params = new LinkedHashMap<String, List<String>>();
		// String name = null;
		// int pos = 0; // 未处理字符开始位置
		//
		// int flag; // 未处理字符结束位置
		//
		// for (flag = 0; flag < url.length(); flag++) {
		// final char current = url.charAt(flag);
		// if ('=' == current && name == null) { // 键值对的分界点
		//
		// if (pos != flag) {
		// name = url.substring(pos, flag);
		// }
		// pos = flag + 1;
		// } else if ('&' == current || ';' == current) { // 参数对的分界点
		//
		// if (name == null && pos != flag) {
		// // 对于像&a&这类无参数值的字符串，我们将name为a的值设为""
		//
		// addParam(params, url.substring(pos, flag), "");
		// } else if (name != null) {
		// addParam(params, name, url.substring(pos, flag));
		// name = null;
		// }
		// pos = flag + 1;
		// }
		// }
		//
		// if (pos != flag) {
		// if (name == null) {
		// addParam(params, url.substring(pos, flag), "");
		// } else {
		// addParam(params, name, url.substring(pos, flag));
		// }
		// } else if (name != null) {
		// addParam(params, name, "");
		// }
		// }
		//
		// return params;
	}

	/**
	 * 
	 * 将表单数据加到URL中（用于GET表单提交）
	 */
	public static String urlWithForm(final String url, final Map<String, Object> form) {
		final String queryString = toParams(form);
		return urlWithForm(url, queryString);
	}

	/**
	 * 
	 * 将表单数据字符串加到URL中（用于GET表单提交）
	 * 
	 */
	public static String urlWithForm(final String url, final String queryString) {
		Assert.notNull(url, "form'data string append to url failure,the url is null");
		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(url);
		if (!StringUtil.isBlank(queryString)) {
			if (url.indexOf('?') != -1) {
				// 原URL已经带参数

				stringBuffer.append('&').append(queryString);
			}
			stringBuffer.append(url.lastIndexOf('?') == url.length() ? queryString : "?" + queryString);
		}

		return stringBuffer.toString();
	}

	/**
	 * 
	 * 从Http连接的头信息中获得字符集
	 * 
	 */
	public static String getCharset(final HttpURLConnection conn) {
		Assert.notNull(conn, "get http connection charset failure,the connection is null");
		final String charset = conn.getContentEncoding();

		return StringUtil.isBlank(charset) ? RegexUtil.get(CHARSET_PATTERN, conn.getContentType() + "\"", 1) : charset;
	}

	/**
	 * 
	 * 从多级反向代理中获得第一个非unknown IP地址
	 */
	public static String getMultistageReverseProxyIp(final String ipStr) {
		Assert.isTrue(!StringUtil.isBlank(ipStr) && ipStr.matches(Validator.IPV4),
				"get real ip from proxy failure,the ip is not invalid");
		String resultIp = "";
		if (ipStr != null && ipStr.indexOf(',') > 0) {
			final String[] ips = ipStr.split(",");
			for (final String subIp : ips) {
				if (!isUnknow(subIp)) {
					resultIp = subIp;
					break;
				}
			}
		}
		return StringUtil.isBlank(resultIp) ? ipStr : resultIp;
	}

	/**
	 * 
	 * 检测给定字符串是否为未知，多用于检测HTTP请求相关
	 * 
	 */
	public static boolean isUnknow(final String checkString) {
		return StringUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
	}

	/**
	 * 
	 * 从流中读取内容
	 * 
	 * reponse读的时候需要依赖这方法，但传入inputstream就得不到页面的charset了
	 * 
	 * 修改下，单纯的通过input和charset来得到string
	 * 
	 */
	// public static String getString(final URL url, final String charset, final
	// boolean useContentCharset) {
	// HttpURLConnection connection;
	// InputStream input;
	//
	// try {
	// connection = (HttpURLConnection) url.openConnection();
	// input = connection.getInputStream();
	// } catch (IOException e1) {
	// throw new ToolException(StringUtil.format("获取url输入流失败: {}",
	// e1.getMessage()), e1);
	// }
	//
	// String result;
	//
	// if (useContentCharset) {
	// final String charsetInContent = getCharset(connection);
	//
	// final StringBuilder content = new StringBuilder();
	//
	// final BufferedReader reader = IoUtil.getReader(input,
	// StringUtil.isBlank(charsetInContent)
	// ? Charset.defaultCharset() : Charset.forName(charsetInContent));
	//
	// String line = null;
	//
	// try {
	// while ((line = reader.readLine()) != null) {
	// content.append(line).append('\n');
	// }
	// } catch (IOException e) {
	// throw new ToolException(StringUtil.format("获取url输入流中的字符串失败: {}",
	// e.getMessage()), e);
	// } finally {
	// IoUtil.close(reader);
	// }
	// result = content.toString();
	// } else {
	// result = IoUtil.read(input, charset);
	// }
	// return result;
	// }

	/**
	 * 
	 * 根据文件扩展名获得MimeType
	 */
	public static String getMimeType(final String filePath) {
		return URLConnection.getFileNameMap().getContentTypeFor(filePath);
	}

	/**
	 * 将键值对加入到值为List类型的Map中
	 */
	private static boolean addParam(final Map<String, List<String>> params, final String name, final String value) {
		List<String> values = params.get(name);
		if (values == null) {
			values = new ArrayList<String>(1); // 一般是一个参数

			params.put(name, values);
		}
		values.add(value);
		return true;
	}
}
