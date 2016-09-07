package com.star.string;

/**
 * 16进制，10进制相互转换
 * 
 * @author starhq
 *
 */
public final class HexUtil {

	private HexUtil() {
	}

	/**
	 * 字节转16进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(final byte[] bytes) {

		final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final int length = bytes.length;
		char[] str = new char[length * 2];
		int flag = 0;
		for (int i = 0; i < length; i++) {
			final byte byte0 = bytes[i];
			str[flag++] = hexDigits[byte0 >>> 4 & 0xF];
			str[flag++] = hexDigits[byte0 & 0xF];
		}

		return new String(str);
	}

	/**
	 * 16进制转字符串
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] hex2Byte(final String str) {
		final int length = str.length() / 2;
		final char[] hexChars = str.toCharArray();
		byte[] out = new byte[length];
		for (int i = 0; i < length; i++) {
			final int pos = i * 2;
			out[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return out;
	}

	/**
	 * 单个数字转换为16进制字符串
	 * 
	 * @param i
	 * @return
	 */
	public static String byte2Hex(final byte data) {

		final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] str = new char[2];
		int flag = 0;
		str[flag++] = hexDigits[data >>> 4 & 0xF];
		str[flag++] = hexDigits[data & 0xF];

		return new String(str);
	}

	private static byte charToByte(final char data) {
		return (byte) "0123456789ABCDEF".indexOf(data);
	}
}
