package com.star.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import com.star.lang.Assert;

/**
 * jdk包时间工具类
 * 
 * java.time
 * 
 * @author starhq
 *
 */
public final class JDK8DateTimeUtil {

	private JDK8DateTimeUtil() {
	}

	/**
	 * 格式化时间
	 * 
	 * ldt为空采用默认当前时间，dtf为空采用yyyy-MM-ddTHH:mm:ss
	 */
	public static String format(final LocalDateTime ldt, final DateTimeFormatter dtf) {
		LocalDateTime date = Objects.isNull(ldt) ? LocalDateTime.now() : ldt;
		DateTimeFormatter format = Objects.isNull(dtf) ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : dtf;
		return date.format(format);
	}

	/**
	 * 解析时间字符串
	 * 
	 * dtf为空采用yyyy-MM-ddTHH:mm:ss
	 */
	public static LocalDateTime parse(final String dateString, final DateTimeFormatter dtf) {
		Assert.notBlank(dateString, "parse string to localdatetime failue,the date string is blank");
		DateTimeFormatter format = Objects.isNull(dtf) ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : dtf;
		return LocalDateTime.parse(dateString, format);
	}

	/**
	 * 指定时间增加一定偏移量
	 * 
	 * ldt为空采用默认当前时间
	 */
	public static LocalDateTime plus(final LocalDateTime ldt, final ChronoUnit unit, final long amountToAdd) {
		LocalDateTime date = Objects.isNull(ldt) ? LocalDateTime.now() : ldt;
		return date.plus(amountToAdd, unit);
	}

	/**
	 * 指定时间减少一定偏移量
	 * 
	 * ldt为空采用默认当前时间
	 */
	public static LocalDateTime minus(final LocalDateTime ldt, final ChronoUnit unit, final long amountToAdd) {
		LocalDateTime date = Objects.isNull(ldt) ? LocalDateTime.now() : ldt;
		return date.minus(amountToAdd, unit);
	}

	/**
	 * 两个时间点相差多少时间单位
	 * 
	 * 开始时间为空采用当前时间,截至时间为空抛异常
	 */
	public static long dateDiff(final LocalDateTime start, final LocalDateTime end, final ChronoUnit unit) {
		Assert.notNull(end, "get tow date's diff failure,the end date is null");
		LocalDateTime date = Objects.isNull(start) ? LocalDateTime.now() : start;

		return date.until(end, unit);
	}

	/**
	 * 午夜
	 * 
	 * ldt为空采用默认当前时间
	 */
	public static LocalDateTime getMidnight(final LocalDateTime ldt) {
		LocalDateTime date = Objects.isNull(ldt) ? LocalDateTime.now() : ldt;
		return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	/**
	 * 23:59:59
	 * 
	 * ldt为空采用默认当前时间
	 */
	public static LocalDateTime getEndOfDay(final LocalDateTime ldt) {
		LocalDateTime date = Objects.isNull(ldt) ? LocalDateTime.now() : ldt;
		return date.withHour(23).withMinute(59).withSecond(59).withNano(999);
	}

}
