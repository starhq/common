package com.star.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * date工具类
 * 
 * @author starhq
 *
 */
public final class DateUtil {

	/** 毫秒 */
	public final static long MILLESECOND = 1;
	/** 每秒钟的毫秒数 */
	public final static long SECOND_MS = MILLESECOND * 1000;
	/** 每分钟的毫秒数 */
	public final static long MINUTE_MS = SECOND_MS * 60;
	/** 每小时的毫秒数 */
	public final static long HOUR_MS = MINUTE_MS * 60;
	/** 每天的毫秒数 */
	public final static long DAY_MS = HOUR_MS * 24;

	/** 标准日期时间格式，精确到分 */
	public final static String YMD = "yyyy-MM-dd";
	/** 标准日期时间格式，精确到分 */
	public final static String YMDHM = "yyyy-MM-dd HH:mm";
	/** 标准日期时间格式，精确到秒 */
	public final static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	/** 标准日期时间格式，精确到毫秒 */
	public final static String YMDHMSS = "yyyy-MM-dd HH:mm:ss.SSS";
	/** HTTP头中日期时间格式 */
	public final static String HEADER = "EEE, dd MMM yyyy HH:mm:ss z";
	/** 标准时间格式 */
	public final static String HMS = "HH:mm:ss";

	private DateUtil() {
	}

	/**
	 * 格式化时间
	 * 
	 * sdf处理多的话别忘了转成threadlocal
	 */
	public static String format(final Date date, final SimpleDateFormat sdf) {
		Assert.notNull(sdf, "format date failure,the sdf is null");
		final Date tmp = Objects.isNull(date) ? new Date() : date;
		return sdf.format(tmp);
	}

	/**
	 * 解析时间字符串
	 * 
	 * sdf处理多的话别忘了转成threadlocal
	 */
	public static Date parse(final String dateString, final SimpleDateFormat sdf) {
		Assert.notNull(sdf, "parse string to date failure,the sdf is null");
		Assert.notBlank(dateString, "parse string to date failure,the date string is blank");
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			throw new ToolException(StringUtil.format("parse date string failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 获取某天的午夜
	 */
	public static Date getBeginTimeOfDay(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(Objects.isNull(date) ? new Date() : date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 
	 * 获取某天的结束时间
	 * 
	 */
	public static Date getEndTimeOfDay(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(Objects.isNull(date) ? new Date() : date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期偏移指定时间后的时间
	 * 
	 * 要注意calendarField的值，暂时没想到办法来约束他
	 */
	public static Date offsiteDate(final Date date, final int calendarField, final int offsite) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(Objects.isNull(date) ? new Date() : date);
		calendar.add(calendarField, offsite);
		return calendar.getTime();
	}

	/**
	 * 两个时间点的时间差,diffField用DAY_MS等常量来除，总感觉不太靠谱
	 */
	public static long diff(final Date start, final Date end, final long diffField) {
		Assert.notNull(end, "get tow date's diff failure,the end date is null");
		Assert.isTrue(diffField <= 0, "get tow date's diff failure，the diffField is invalid");

		final Date temp = Objects.isNull(start) ? new Date() : start;

		final long diff = Math.abs(temp.getTime() - end.getTime());
		return diff / diffField;
	}

	/**
	 * 计时，常用于记录某段代码的执行时间，单位：毫秒
	 */
	public static long spendMs(final long preTime) {
		return System.currentTimeMillis() - preTime;
	}
}
