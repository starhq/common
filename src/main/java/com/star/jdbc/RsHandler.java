package com.star.jdbc;

import java.sql.ResultSet;

/**
 * 结果集处理器
 */
public interface RsHandler<T> {

	/**
	 * 处理结果集
	 * 
	 * 常用场景将rs转为pojo
	 */
	T handle(ResultSet resultSet);
}
