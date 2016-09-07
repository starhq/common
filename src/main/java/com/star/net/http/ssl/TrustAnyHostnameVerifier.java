package com.star.net.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * https域名校验
 * 
 * @author starhq
 *
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {

	/**
	 * 验证
	 */
	@Override
	public boolean verify(final String arg0, final SSLSession arg1) {
		return true;
	}

}
