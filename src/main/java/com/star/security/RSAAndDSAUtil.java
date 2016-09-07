package com.star.security;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * rsa和dsa通用加解�?
 * 
 * @author starhq
 *
 */
public final class RSAAndDSAUtil extends BaseSecureUtil {

	/**
	 * 默认种子
	 */
	private static final String DEFAULT_SEED = "0f22507a10bbddd07d8a3082122966e3";

	/**
	 * 密钥长度
	 */
	private static final int KEY_SIZE = 1024;

	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	private RSAAndDSAUtil() {
		super();
	}

	/**
	 * 生成密钥�?
	 */
	public static KeyPair initKeyPair(final ALGORITHM algorithm) {
		return initKeyPari(DEFAULT_SEED, algorithm);
	}

	/**
	 * 生成密钥�?
	 */
	public static KeyPair initKeyPari(final String seed, final ALGORITHM algorithm) {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(algorithm.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new ToolException(StringUtil.format("init keypair failure,the reason is: {}", e.getMessage()), e);
		}
		SecureRandom secureRandom;
		if (!StringUtil.isBlank(seed)) {
			secureRandom = new SecureRandom();
			secureRandom.setSeed(seed.getBytes());
			kpg.initialize(KEY_SIZE, secureRandom);
		} else {
			kpg.initialize(KEY_SIZE);
		}

		return kpg.generateKeyPair();
	}

	/**
	 * 用私钥对信息生成数字签名
	 */
	public static byte[] sign(final byte[] data, final String privateKey, final ALGORITHM algorithm) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			final PrivateKey prikey = (PrivateKey) toKey(HexUtil.hex2Byte(privateKey), algorithm, false);
			try {
				final Signature signature = Signature.getInstance(algorithm.toString());
				signature.initSign(prikey);
				signature.update(data);
				results = signature.sign();
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
				throw new ToolException(
						StringUtil.format("use privatekey to verify sign failure,the reason is: {}", e.getMessage()),
						e);
			}
		} else {
			results = new byte[0];
		}
		return results;
	}

	/**
	 * 校验数字签名
	 */
	public static boolean verify(final byte[] data, final String publicKey, final String sign,
			final ALGORITHM algorithm) {
		Boolean result;
		if (!ArrayUtil.isEmpty(data)) {
			// 取公钥匙对象
			final PublicKey pubKey = (PublicKey) toKey(HexUtil.hex2Byte(publicKey), algorithm, true);

			try {
				final Signature signature = Signature.getInstance(algorithm.toString());
				signature.initVerify(pubKey);
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
	 * 解密
	 */
	public static byte[] decrypt(final byte[] data, final String key, final ALGORITHM algorithm, final Boolean pub) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, pub);

			try {
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
				// 正常流程
				// results = cipher.doFinal(data);

				// 超长了需要分段处理
				final int inputLen = data.length;
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				int offSet = 0;
				byte[] cache;
				int step = 0;
				// 对数据分段解密
				while (inputLen - offSet > 0) {
					if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
						cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
					} else {
						cache = cipher.doFinal(data, offSet, inputLen - offSet);
					}
					out.write(cache, 0, cache.length);
					step++;
					offSet = step * MAX_DECRYPT_BLOCK;
				}
				results = out.toByteArray();
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
	public static byte[] encrypt(final byte[] data, final String key, final ALGORITHM algorithm, final Boolean pub) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, pub);
			try {
				final Cipher cipher = Cipher.getInstance(algorithm.toString());
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);

				// 正常流程
				// results = cipher.doFinal(data);

				// 超长了需要分段处理
				final int inputLen = data.length;
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				int offSet = 0;
				byte[] cache;
				int step = 0;
				// 对数据分段解密
				while (inputLen - offSet > 0) {
					if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
						cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
					} else {
						cache = cipher.doFinal(data, offSet, inputLen - offSet);
					}
					out.write(cache, 0, cache.length);
					step++;
					offSet = step * MAX_DECRYPT_BLOCK;
				}
				results = out.toByteArray();
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
