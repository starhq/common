package com.star.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.star.exception.pojo.ToolException;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * 加密工具类基类，包含单向加密
 * 
 * 该类方法来自 http://snowolf.iteye.com/blog/379860
 * 
 * @author starhq
 *
 */
public class BaseSecureUtil {

	/**
	 * 构造器
	 */
	protected BaseSecureUtil() {
		super();
	}

	/**
	 * 算法种类 MD5,SHA1 HmacMD5, HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512, DES
	 * key size must be equal to 56 DESede(TripleDES) key size must be equal to
	 * 112 or 168 bits may not be available Blowfish key size must be multiple
	 * of 8, and can only range from 32 to 448 (inclusive) RC2 key size must be
	 * between 40 and 1024 bits RC4(ARCFOUR) key size must be between 40 and
	 * 1024 bits MD5withRSA 签名用
	 */
	public enum ALGORITHM {
		MD5, SHA1, HmacMD5, HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512, DES, DESede, AES, Blowfish, RC2, RC4, PBEWITHMD5andDES, PBEWithMD5AndTripleDES, PBEWithSHA1AndDESede, PBEWithSHA1AndRC2_40, RSA, MD5withRSA, DH, DSA;
	}

	/**
	 * 初始化密钥，就一把密钥的情况
	 * 
	 * @param algorithm
	 * @return
	 */
	public static Key initKey(final boolean useSeed, final String seed, final ALGORITHM algorithm) {
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance(algorithm.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new ToolException(
					StringUtil.format("init {}'s key failure the reason is: {}", algorithm.toString(), e.getMessage()),
					e);
		}

		if (useSeed) {
			SecureRandom secureRandom;
			if (!StringUtil.isBlank(seed)) {
				secureRandom = new SecureRandom(HexUtil.hex2Byte(seed));
			} else {
				secureRandom = new SecureRandom();
			}
			keyGenerator.init(secureRandom);
		}
		return keyGenerator.generateKey();
	}

	/**
	 * 单向加密的，如md5，sha1等
	 */
	public static byte[] digest(final String data, final ALGORITHM algorithm) {
		byte[] result;
		if (!StringUtil.isBlank(data)) {
			try {
				final MessageDigest messageDigest = MessageDigest.getInstance(algorithm.toString());
				messageDigest.update(data.getBytes());
				result = messageDigest.digest();
			} catch (NoSuchAlgorithmException e) {
				throw new ToolException(
						StringUtil.format("{} digest failure,the reason is: {}", algorithm.toString(), e.getMessage()),
						e);
			}

		} else {
			result = new byte[0];
		}
		return result;
	}

	/**
	 * hmac加密
	 */
	public static byte[] encryptHMAC(final String data, final Key secretKey) {
		byte[] result;
		if (!StringUtil.isBlank(data)) {
			try {
				final Mac mac = Mac.getInstance(secretKey.getAlgorithm());
				mac.init(secretKey);
				result = mac.doFinal(HexUtil.hex2Byte(data));
			} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException e) {
				throw new ToolException(StringUtil.format("{} digest failure,the reason is: {}",
						secretKey.getAlgorithm(), e.getMessage()), e);
			}

		} else {
			result = new byte[0];
		}

		return result;
	}

	/**
	 * 根据字节数组还原成key
	 * 
	 */
	protected static Key toKey(final byte[] key, final ALGORITHM algorithm, final Boolean pub) {
		Key secretKey;
		SecretKeyFactory keyFactory;
		KeyFactory signKeyFactory;
		switch (algorithm) {
		case DES:
			try {
				keyFactory = SecretKeyFactory.getInstance(algorithm.toString());
				final DESKeySpec dks = new DESKeySpec(key);
				secretKey = keyFactory.generateSecret(dks);
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new ToolException(StringUtil.format("restore key failue,the reason is: {}", e.getMessage()), e);
			}
			break;
		case PBEWITHMD5andDES:
		case PBEWithMD5AndTripleDES:
		case PBEWithSHA1AndDESede:
		case PBEWithSHA1AndRC2_40:
			final String password = new String(key);
			try {
				keyFactory = SecretKeyFactory.getInstance(algorithm.toString());
				final PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
				secretKey = keyFactory.generateSecret(keySpec);
				break;
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new ToolException(StringUtil.format("restore key failue,the reason is: {}", e.getMessage()), e);
			}
		case RSA:
			try {
				signKeyFactory = KeyFactory.getInstance(algorithm.toString());
				EncodedKeySpec baseKeySpec;
				if (pub) {
					baseKeySpec = new X509EncodedKeySpec(key);
					secretKey = signKeyFactory.generatePublic(baseKeySpec);
				} else {
					baseKeySpec = new PKCS8EncodedKeySpec(key);
					secretKey = signKeyFactory.generatePrivate(baseKeySpec);
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new ToolException(StringUtil.format("restore key failue,the reason is: {}", e.getMessage()), e);
			}
			break;
		default:
			secretKey = new SecretKeySpec(key, algorithm.toString());
			break;
		}

		return secretKey;
	}

}
