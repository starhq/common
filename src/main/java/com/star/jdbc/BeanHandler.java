package com.star.jdbc;

import java.sql.ResultSet;

/**
 * rs转为pojo
 * 
 * 借鉴dbutils
 * 
 * @author starhq
 */
public class BeanHandler<T> implements RsHandler<T> {

	/**
	 * 要转的类型
	 */
	private transient final Class<T> type;

	/**
	 * 解析器
	 */
	private transient final BeanProcessor processor = new BeanProcessor();

	/**
	 * 构造方法
	 */
	public BeanHandler(final Class<T> type) {
		super();
		this.type = type;
	}

	/**
	 * result转换
	 */
	@Override
	public T handle(final ResultSet resultSet) {
		return processor.toBean(resultSet, type);
	}

}
