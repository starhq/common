package com.star.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.PBEParameterSpec;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * pbe加解密
 * 
 * @author starhq
 *
 */
public final class PBEUtil extends BaseSecureUtil {

	/**
	 * 密钥长度
	 */
	public static final int KEY_SIZE = 1024;

	private PBEUtil() {
		super();
	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(final byte[] data, final String password, final byte[] salt,
			final ALGORITHM algorithm) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data) && !ArrayUtil.isEmpty(salt)) {
			final Key secretKey = toKey(password.getBytes(), algorithm, null);

			try {
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				final PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
				cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

				results = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				throw new ToolException(StringUtil.format("decrypt failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			results = new byte[0];
		}
		return results;
	}

	/**
	 * 加密
	 * 
	 */
	public static byte[] encrypt(final byte[] data, final String password, final byte[] salt,
			final ALGORITHM algorithm) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data) && !ArrayUtil.isEmpty(salt)) {
			final Key secretKey = toKey(password.getBytes(), algorithm, null);
			try {
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				final PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

				results = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				throw new ToolException(StringUtil.format("decrypt failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			results = new byte[0];
		}
		return results;
	}
}
