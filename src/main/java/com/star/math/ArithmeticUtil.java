package com.star.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 精确计算
 * 
 * @author http://wdbk.iteye.com/blog/836072
 *
 */
public final class ArithmeticUtil {

	private ArithmeticUtil() {
	}

	/**
	 * 提供精确的加法运算
	 * 
	 * scale大于0，精确到sacle位小数，小于0不做处理
	 */
	public static BigDecimal add(final String value1, final String value2, final int scale) {
		Assert.isTrue(!StringUtil.isBlank(value1) && !StringUtil.isBlank(value2),
				"plus failure,the input parameter is null");
		final BigDecimal bigDecimal1 = new BigDecimal(value1);
		final BigDecimal bigDecimal2 = new BigDecimal(value2);
		return scale > 0 ? bigDecimal1.add(bigDecimal2).setScale(scale, BigDecimal.ROUND_HALF_UP)
				: bigDecimal1.add(bigDecimal2);
	}

	/**
	 * 提供精确的减法运算
	 * 
	 * scale大于0，精确到sacle位小数，小于0不做处理
	 */
	public static BigDecimal subtract(final String value1, final String value2, final int scale) {
		Assert.isTrue(!StringUtil.isBlank(value1) && !StringUtil.isBlank(value2),
				"subtract failure,the input parameter is null");
		final BigDecimal bigDecimal1 = new BigDecimal(value1);
		final BigDecimal bigDecimal2 = new BigDecimal(value2);
		return scale > 0 ? bigDecimal1.subtract(bigDecimal2).setScale(scale, RoundingMode.HALF_UP)
				: bigDecimal1.subtract(bigDecimal2);
	}

	/**
	 * 提供精确的乘法运算
	 * 
	 * scale大于0，精确到sacle位小数，小于0不做处理
	 */
	public static BigDecimal multiply(final String value1, final String value2, final int scale) {
		Assert.isTrue(!StringUtil.isBlank(value1) && !StringUtil.isBlank(value2),
				"multiply failure,the input parameter is null");
		final BigDecimal bigDecimal1 = new BigDecimal(value1);
		final BigDecimal bigDecimal2 = new BigDecimal(value2);
		return scale > 0 ? bigDecimal1.multiply(bigDecimal2).setScale(scale, BigDecimal.ROUND_HALF_UP)
				: bigDecimal1.multiply(bigDecimal2);
	}

	/**
	 * 提供精确的除法运算
	 * 
	 * scale大于0，精确到sacle位小数，小于0不做处理
	 */
	public static BigDecimal divide(final String value1, final String value2, final int scale) {
		Assert.isTrue(!StringUtil.isBlank(value1) && !StringUtil.isBlank(value2),
				"divide failure,the input parameter is null");
		final BigDecimal bigDecimal1 = new BigDecimal(value1);
		final BigDecimal bigDecimal2 = new BigDecimal(value2);
		return scale > 0 ? bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP)
				: bigDecimal1.divide(bigDecimal2);
	}

	/**
	 * 提供精确的取余数
	 * 
	 * scale大于0，精确到sacle位小数，小于0不做处理
	 */
	public static BigDecimal remainder(final String value1, final String value2, final int scale) {
		Assert.isTrue(!StringUtil.isBlank(value1) && !StringUtil.isBlank(value2),
				"remainder failure,the input parameter is null");
		final BigDecimal bigDecimal1 = new BigDecimal(value1);
		final BigDecimal bigDecimal2 = new BigDecimal(value2);
		return scale > 0 ? bigDecimal1.remainder(bigDecimal2).setScale(scale, BigDecimal.ROUND_HALF_UP)
				: bigDecimal1.remainder(bigDecimal2);
	}

	/**
	 * 四舍五入
	 * 
	 */
	public static BigDecimal round(final String value, final int scale) {
		Assert.notBlank(value, "round failue,the input parameter is null");
		Assert.isTrue(scale > 0, "round failue,the input scale must bigger than zero");
		final BigDecimal bigDecimal = new BigDecimal(value);
		return bigDecimal.setScale(scale, RoundingMode.HALF_UP);
	}

}
