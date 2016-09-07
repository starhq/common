package com.star.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * 证书加解密工具类
 * 
 * @author starhq
 *
 */
public final class CertificateUtil extends BaseSecureUtil {

	/**
	 * Java密钥�?(Java Key Store，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";

	/**
	 * 
	 */
	public static final String X509 = "X.509";

	/**
	 * 
	 */
	public static final String SUNX509 = "SunX509";

	/**
	 * 
	 */
	public static final String SSL = "SSL";

	private CertificateUtil() {
		super();
	}

	/**
	 * 由KeyStore获得私钥
	 */
	private static PrivateKey getPrivateKey(final String keyStorePath, final String alias, final String password) {
		try {
			final KeyStore keyStore = getKeyStore(keyStorePath, password);
			return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new ToolException(
					StringUtil.format("get private from keystore failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 由Certificate获得公钥
	 */
	private static PublicKey getPublicKey(final String certificatePath) {
		final Certificate certificate = getCertificate(certificatePath);
		return certificate.getPublicKey();
	}

	/**
	 * 获得Certificate
	 */
	private static Certificate getCertificate(final String certificatePath) {
		try {
			final CertificateFactory certFactory = CertificateFactory.getInstance(X509);
			final FileInputStream inputStream = new FileInputStream(certificatePath);

			final Certificate certificate = certFactory.generateCertificate(inputStream);
			inputStream.close();

			return certificate;
		} catch (CertificateException | IOException e) {
			throw new ToolException(StringUtil.format("get certificate failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 获得Certificate
	 */
	private static Certificate getCertificate(final String keyStorePath, final String alias, final String password) {
		try {
			final KeyStore keyStore = getKeyStore(keyStorePath, password);
			return keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			throw new ToolException(StringUtil.format("get certificate failure,the reason is: {}", e.getMessage()), e);
		}

	}

	/**
	 * 获得KeyStore
	 */
	private static KeyStore getKeyStore(final String keyStorePath, final String password) {
		try {
			final FileInputStream fileInputStream = new FileInputStream(keyStorePath);
			final KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
			keyStore.load(fileInputStream, password.toCharArray());
			fileInputStream.close();
			return keyStore;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new ToolException(StringUtil.format("get keystore failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(final byte[] data, final String path, final String alias, final String password,
			final Boolean pub) {
		byte[] result;
		if (!ArrayUtil.isEmpty(data)) {
			Key key;
			if (pub) {
				key = getPrivateKey(path, alias, password);
			} else {
				key = getPublicKey(path);
			}
			try {
				final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
				cipher.init(Cipher.DECRYPT_MODE, key);

				result = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				throw new ToolException(StringUtil.format("decrypt failure,the reason is: {}", e.getMessage()), e);
			}

		} else {
			result = new byte[0];
		}

		return result;
	}

	/**
	 * 加密
	 */
	public static byte[] encrypt(final byte[] data, final String path, final String alias, final String password,
			final Boolean pub) {
		byte[] result;
		if (!ArrayUtil.isEmpty(data)) {
			Key key;
			if (pub) {
				key = getPrivateKey(path, alias, password);
			} else {
				key = getPublicKey(path);
			}
			try {
				final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
				cipher.init(Cipher.ENCRYPT_MODE, key);

				result = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				throw new ToolException(StringUtil.format("encrypt failure: {}", e.getMessage()), e);
			}

		} else {
			result = new byte[0];
		}

		return result;
	}

	/**
	 * 验证Certificate
	 */
	public static boolean verifyCertificate(final String certificatePath) {
		return verifyCertificate(new Date(), certificatePath);
	}

	/**
	 * 验证Certificate是否过期或无�?
	 */
	public static boolean verifyCertificate(final Date date, final String certificatePath) {
		// 取得证书
		final Certificate certificate = getCertificate(certificatePath);
		// 验证证书是否过期或无�?
		return verifyCertificate(date, (X509Certificate) certificate);
	}

	/**
	 * 验证证书是否过期或无�?
	 * 
	 * @param date
	 * @param certificate
	 * @return
	 */
	private static boolean verifyCertificate(final Date date, final X509Certificate certificate) {
		boolean status = true;
		try {
			certificate.checkValidity(date);
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
			status = false;
		}
		return status;
	}

	/**
	 * 签名
	 */
	public static byte[] sign(final byte[] sign, final String keyStorePath, final String alias, final String password) {
		byte[] results;
		if (!ArrayUtil.isEmpty(sign)) {
			// 获得证书
			final X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
			// 获取私钥
			final KeyStore keyStore = getKeyStore(keyStorePath, password);
			try {
				// 取得私钥
				final PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());

				// 构建签名
				final Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
				signature.initSign(privateKey);
				signature.update(sign);
				results = signature.sign();
			} catch (UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException
					| SignatureException e) {
				throw new ToolException(StringUtil.format("sign failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			results = new byte[0];
		}
		return results;
	}

	/**
	 * 验证签名
	 */
	public static boolean verify(final byte[] data, final String sign, final String certificatePath) {
		Boolean result;
		if (!ArrayUtil.isEmpty(data)) {
			// 获得证书
			final X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
			// 获得公钥
			final PublicKey publicKey = x509Certificate.getPublicKey();
			try {
				final Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
				signature.initVerify(publicKey);
				signature.update(data);

				result = signature.verify(HexUtil.hex2Byte(sign));
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
				throw new ToolException(StringUtil.format("verify sign failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			result = false;
		}

		return result;

	}

	/**
	 * 验证Certificate
	 */
	public static boolean verifyCertificate(final Date date, final String keyStorePath, final String alias,
			final String password) {
		final Certificate certificate = getCertificate(keyStorePath, alias, password);
		return verifyCertificate(date, (X509Certificate) certificate);
	}

	/**
	 * 验证Certificate
	 */
	public static boolean verifyCertificate(final String keyStorePath, final String alias, final String password) {
		return verifyCertificate(new Date(), keyStorePath, alias, password);
	}

	/**
	 * 获得SSLSocektFactory
	 */
	public static SSLSocketFactory getSSLSocketFactory(final String password, final String keyStorePath,
			final String trustKeyStorePath) {
		try {
			final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(SUNX509);
			final KeyStore keyStore = getKeyStore(keyStorePath, password);
			keyManagerFactory.init(keyStore, password.toCharArray());
			final TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(SUNX509);
			final KeyStore trustkeyStore = getKeyStore(trustKeyStorePath, password);
			tmFactory.init(trustkeyStore);
			final SSLContext ctx = SSLContext.getInstance(SSL);
			ctx.init(keyManagerFactory.getKeyManagers(), tmFactory.getTrustManagers(), null);
			final SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();

			return sslSocketFactory;
		} catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new ToolException(StringUtil.format("get sslsokcetfactory failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 为HttpsURLConnection配置SSLSocketFactory
	 */
	public static void configSSLSocketFactory(final HttpsURLConnection conn, final String password,
			final String keyStorePath, final String trustKeyStorePath) {
		conn.setSSLSocketFactory(getSSLSocketFactory(password, keyStorePath, trustKeyStorePath));
	}
}
