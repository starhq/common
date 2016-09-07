package com.star.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * rsa加解�?
 * 
 * @author starhq
 *
 */
public final class DHUtil extends BaseSecureUtil {

	/**
	 * 密钥长度
	 */
	private static final int KEY_SIZE = 1024;

	private DHUtil() {
		super();
	}

	/**
	 * 生成甲方密钥�?
	 */
	public static KeyPair initKeyPair(final ALGORITHM algorithm) {
		try {
			final KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm.toString());
			kpg.initialize(KEY_SIZE);
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new ToolException(
					StringUtil.format("init party a's keyparir failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 生成乙方密钥�?,key甲方公钥
	 */
	public static KeyPair initKeyPair(final String key, final ALGORITHM algorithm) {
		final DHPublicKey pubKey = (DHPublicKey) toKey(HexUtil.hex2Byte(key), algorithm, true);
		final DHParameterSpec dhParamSpec = pubKey.getParams();

		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(algorithm.toString());
			keyPairGenerator.initialize(dhParamSpec);
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new ToolException(
					StringUtil.format("init party b's keypair failure,the reason is: {}", e.getMessage()), e);
		}

		return keyPairGenerator.generateKeyPair();

	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(final byte[] data, final String key, final ALGORITHM algorithm, final Boolean pub) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			try {
				final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, pub);

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
	public static byte[] encrypt(final byte[] data, final String key, final ALGORITHM algorithm, final Boolean pub) {
		byte[] results;
		if (!ArrayUtil.isEmpty(data)) {
			try {
				final Key secretKey = toKey(HexUtil.hex2Byte(key), algorithm, pub);
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

	/**
	 * 构建密钥
	 */
	private static SecretKey getSecretKey(final String publicKey, final String privateKey, final ALGORITHM algorithm) {
		final PublicKey pubKey = (PublicKey) toKey(HexUtil.hex2Byte(publicKey), algorithm, true);

		final PrivateKey priKey = (PrivateKey) toKey(HexUtil.hex2Byte(privateKey), algorithm, false);

		try {
			final KeyAgreement keyAgree = KeyAgreement.getInstance(algorithm.toString());
			keyAgree.init(priKey);
			keyAgree.doPhase(pubKey, true);

			return keyAgree.generateSecret(algorithm.toString());
		} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException e) {
			throw new ToolException(StringUtil.format("init secretkey failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 加密<br>
	 */
	public static byte[] encrypt(final byte[] data, final String publicKey, final String privateKey,
			final ALGORITHM algorithm) {
		byte[] result;
		if (!ArrayUtil.isEmpty(data)) {
			// 生成本地密钥
			final SecretKey secretKey = getSecretKey(publicKey, privateKey, algorithm);

			// 数据加密
			try {
				final Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);

				result = cipher.doFinal(data);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				throw new ToolException(StringUtil.format("encrypt failure,the reason is: {}", e.getMessage()), e);
			}
		} else {
			result = new byte[0];
		}
		return result;
	}

	/**
	 * 解密<br>
	 */
	public static byte[] decrypt(final byte[] data, final String publicKey, final String privateKey,
			final ALGORITHM algorithm) {

		byte[] result;

		if (!ArrayUtil.isEmpty(data)) {
			try {
				// 生成本地密钥
				final SecretKey secretKey = getSecretKey(publicKey, privateKey, algorithm);
				// 数据解密
				final Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
				cipher.init(Cipher.DECRYPT_MODE, secretKey);

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
}
