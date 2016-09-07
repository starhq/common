package com.star.net.http;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.star.collection.ArrayUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 操作cookie的工具类
 * 
 * @author starhq
 *
 */
public final class CookieUtil {

	/**
	 * 设置cookie有效期，根据需要自定义[本系统设置为7天]
	 */
	private final static int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

	/**
	 * 设置默认路径：/，这个路径即该工程下都可以访问该cookie 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
	 */
	private final static String COOKIE_PATH = "/";

	private CookieUtil() {
	}

	/**
	 * 从request获得cookie
	 */
	public static Cookie getCookie(final HttpServletRequest request, final String name) {
		Assert.notNull(request, "get cookie is null,the request is null");
		Assert.notBlank(name, "get cookie failure,the cookie name is blank");
		final Cookie[] cookies = request.getCookies();

		Cookie result = null;

		if (!ArrayUtil.isEmpty(cookies)) {
			for (final Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					result = cookie;
				}
				if (request.getServerName().equals(cookie.getDomain())) {
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 获得cookie中name键的value
	 */
	public static String getCookieValue(final HttpServletRequest request, final String name) {
		final Cookie cookie = getCookie(request, name);
		return Objects.isNull(cookie) ? "" : cookie.getValue();
	}

	/**
	 * 设置cookie
	 * 
	 * domain之前没用过，不知道起啥作用，留着扩展用
	 * 
	 */
	public static void setCookie(final HttpServletResponse response, final String name, final String value,
			final int maxAge, final String domain) {
		Assert.notNull(response, "set cookie is null,the input response is null");
		Assert.notBlank(name, "set cookie failure,the input cookie name is blank");

		String val = value;
		if (StringUtil.isBlank(val)) {
			val = "";
		}
		final Cookie cookie = new Cookie(name, val);
		cookie.setMaxAge(maxAge > 0 ? maxAge : COOKIE_MAX_AGE);
		cookie.setPath(COOKIE_PATH);
		if (StringUtil.isBlank(domain)) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}

	/**
	 * 删除cookie
	 * 
	 * domain之前没用过，不知道起啥作用，留着扩展用
	 */
	public static void removeCookie(final HttpServletResponse response, final String name, final String domain) {
		setCookie(response, name, "", 0, domain);
	}
}
