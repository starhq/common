package com.star.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 随机工具类
 * 
 * @author starhq
 *
 */
public final class RandomUtil {

	/** 用于随机选的数字 */
	private static final String NUMBERCHAR = "0123456789";
	/** 用于随机选的字符 */
	private static final String LETTERCHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/** 用于随机选的字符和数字 */
	private static final String ALLCHAR = NUMBERCHAR + LETTERCHAR;

	/**
	 * random初始化
	 */
	private static final Random RANDOM = new Random();

	private RandomUtil() {
	}

	/**
	 * 获得指定范围内的随机数
	 */
	public static int randomInt(final int min, final int max) {
		return RANDOM.nextInt(max - min) + min;
	}

	/**
	 * 获得随机数
	 */
	public static int randomInt() {
		return RANDOM.nextInt();
	}

	/**
	 * 获得指定范围内的随机数 [0,limit)
	 */
	public static int randomInt(final int limit) {
		return RANDOM.nextInt(limit);
	}

	/**
	 * 随机从list中获取元素
	 */
	public static <T> T randomEle(final List<T> list, final int limit) {
		return list.get(randomInt(limit));
	}

	/**
	 * 随机从list中获取几个元素
	 */
	public static <T> List<T> randomEles(final List<T> list, final int count) {
		final List<T> result = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			result.add(randomEle(list, list.size()));
		}
		return result;
	}

	/**
	 * 获得一个随机的字符串（只包含数字和字符）
	 */
	public static String randomAll(final int length) {
		return randomString(ALLCHAR, length);
	}

	/**
	 * 获得一个随机的字符串（只包含数字）
	 */
	public static String randomNumbers(final int length) {
		return randomString(NUMBERCHAR, length);
	}

	/**
	 * 获得一个随机的字符串（只包含字母）
	 */
	public static String randomLetters(final int length) {
		return randomString(LETTERCHAR, length);
	}

	/**
	 * 获得一个随机的字符串
	 */
	public static String randomString(final String baseString, final int length) {
		final StringBuilder builder = new StringBuilder(length);

		final int tmp = length > 0 ? length : 1;

		for (int i = 0; i < tmp; i++) {
			builder.append(baseString.charAt(randomInt(baseString.length())));
		}

		return builder.toString();
	}

	/**
	 * 生成定长的char,比如门锁通信的场景，尾数不够补0
	 */
	public static String generateString(final char cha, final int length) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(cha);
		}
		return builder.toString();
	}

}
