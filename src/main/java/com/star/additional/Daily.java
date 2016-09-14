package com.star.additional;

import com.star.string.StringUtil;

/**
 * 一些日常生活中用到的方法
 * 
 * @author starhq
 *
 */
public final class Daily {

	private Daily() {
	}

	/**
	 * 功能描述：判断是否为质数
	 * 
	 * @param x
	 *            要判断的数
	 * @return 是否为质数
	 */
	public static boolean isPrime(int x) {
		if (x <= 7) {
			if (x == 2 || x == 3 || x == 5 || x == 7)
				return true;
		}
		int c = 7;
		if (x % 2 == 0)
			return false;
		if (x % 3 == 0)
			return false;
		if (x % 5 == 0)
			return false;
		int end = (int) Math.sqrt(x);
		while (c <= end) {
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 6;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 6;
		}
		return true;
	}

	/**
	 * 功能描述：人民币转成大写
	 * 
	 * @param str
	 *            人民币金额
	 * @return 大写金额
	 */
	public static String hangeToBig(String str) {
		double value;
		try {
			value = Double.parseDouble(str.trim());
		} catch (Exception e) {
			return null;
		}
		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示

		char[] vunit = { '万', '亿' }; // 段名表示

		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示

		long midVal = (long) (value * 100); // 转化成整形

		String valStr = String.valueOf(midVal); // 转化成字符串

		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分

		String rail = valStr.substring(valStr.length() - 2); // 取小数部分

		String prefix = ""; // 整数部分转化的结果

		String suffix = ""; // 小数部分转化的结果

		// 处理小数点后面的数

		if (rail.equals("00")) { // 如果小数部分为0

			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来

		}
		// 处理小数点前面的数

		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组

		char zero = '0'; // 标志'0'表示出现过0

		byte zeroSerNum = 0; // 连续出现0的次数

		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字

			int idx = (chDig.length - i - 1) % 4; // 取段内位置

			int vidx = (chDig.length - i - 1) / 4; // 取段位置

			if (chDig[i] == '0') { // 如果当前字符是0

				zeroSerNum++; // 连续0次数递增

				if (zero == '0') { // 标志

					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零

			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的

				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示

			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿

			}
		}

		if (prefix.length() > 0)
			prefix += '圆'; // 如果整数部分存在,则有圆的字样

		return prefix + suffix; // 返回正确表示

	}

	/**
	 * 把手机叫转成139****1234
	 * 
	 * @param str
	 *            原手机号
	 * @return 处理过的手机号
	 */
	public static String getPhoneNumber(final String str) {
		String result = str;
		if (StringUtil.isBlank(result)) {
			result = result.trim();
			result = result.substring(0, 3) + "****" + result.substring(7, str.length());
		}
		return result;
	}
}
