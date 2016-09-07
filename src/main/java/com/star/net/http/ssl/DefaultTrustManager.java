package com.star.net.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 证书管理
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public class DefaultTrustManager implements X509TrustManager {

	/**
	 * 空实现
	 */
	@Override
	public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
		// 嘛都不干
	}

	/**
	 * 空实现
	 */
	@Override
	public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
		// 嘛都不干
	}

	/**
	 * 空实现
	 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// 嘛都不干
		return new X509Certificate[0];
	}

}
