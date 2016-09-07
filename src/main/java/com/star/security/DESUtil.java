package com.star.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * des加解密
 * 
 * @author starhq
 *
 */
public final class DESUtil extends BaseSecureUtil {

	private DESUtil() {
		super();
	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(final byte[] data, final String key, final ALGORITHM algorithm) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, null);

			try {
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				cipher.init(Cipher.DECRYPT_MODE, secretKey);

				results = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
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
	public static byte[] encrypt(final byte[] data, final String key, final ALGORITHM algorithm) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			try {
				final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, null);
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);

				results = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				throw new ToolException(StringUtil.format("encrypt failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			results = new byte[0];
		}
		return results;
	}

}
