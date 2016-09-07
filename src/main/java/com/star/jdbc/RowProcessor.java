package com.star.jdbc;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * 处理结果集的列
 *
 */
public interface RowProcessor {

	/**
	 * 结果集封装成数组
	 */
	Object[] toArray(ResultSet resultSet);

	/**
	 * 结果集转javabean
	 */
	<T> T toBean(ResultSet resultSet, Class<T> type);

	/**
	 * 结果集转list
	 */
	<T> List<T> toBeanList(ResultSet resultSet, Class<T> type);

	/**
	 * 结果集转map
	 */
	Map<String, Object> toMap(ResultSet resultSet);
}
