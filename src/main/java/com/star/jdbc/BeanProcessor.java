package com.star.jdbc;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.star.beans.BeanUtils;
import com.star.clazz.ClassUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * pojo处理的实现
 * 
 * @author starhq
 *
 */
public class BeanProcessor implements RowProcessor {

	/**
	 * 结果集转数组失败
	 */
	@Override
	public Object[] toArray(final ResultSet resultSet) {
		try {
			final ResultSetMetaData meta = resultSet.getMetaData();
			final int cols = meta.getColumnCount();
			final Object[] result = new Object[cols];

			for (int i = 0; i < cols; i++) {
				result[i] = resultSet.getObject(i + 1);
			}

			return result;
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("resultset convert to object array failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * resultse转换为bean
	 */
	@Override
	public <T> T toBean(final ResultSet resultSet, final Class<T> type) {
		final PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(type);

		final int[] columnToProperty = this.mapColumnsToProperties(resultSet, props);

		return createBean(resultSet, type, columnToProperty, props);
	}

	/**
	 * resultset转换为集合
	 */
	@Override
	public <T> List<T> toBeanList(final ResultSet resultSet, final Class<T> type) {
		List<T> results;

		try {
			if (resultSet.next()) {
				final PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(type);
				final int[] columnToProperty = mapColumnsToProperties(resultSet, props);

				results = new ArrayList<>(columnToProperty.length);

				do {
					results.add(createBean(resultSet, type, columnToProperty, props));
				} while (resultSet.next());
			} else {
				results = Collections.emptyList();
			}
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("resultset convert to collection failure,the reason is: {}", e.getMessage()), e);
		}
		return results;
	}

	/**
	 * 结果集转map
	 */
	@Override
	public Map<String, Object> toMap(final ResultSet resultSet) {
		try {
			final ResultSetMetaData rsmd = resultSet.getMetaData();
			final int cols = rsmd.getColumnCount();
			final Map<String, Object> result = new ConcurrentHashMap<>(cols);

			for (int i = 1; i <= cols; i++) {
				result.put(rsmd.getColumnName(i), resultSet.getObject(i));
			}

			return result;
		} catch (SQLException e) {

			throw new ToolException(
					StringUtil.format("resultset convert to map failure,the reason is: {}", e.getMessage()), e);
		}

	}

	/**
	 * 从class获得的PropertyDescriptor和ResultSetMetaData顺序不一样
	 * 要从rsmd来获得PropertyDescriptor对应的字段下标
	 */
	private int[] mapColumnsToProperties(final ResultSet resultSet, final PropertyDescriptor... props) {
		ResultSetMetaData rsmd;
		try {
			rsmd = resultSet.getMetaData();
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("get resultsetmetadata failure,the reason is: {}", e.getMessage()), e);
		}

		int cols;
		try {
			cols = rsmd.getColumnCount();
		} catch (SQLException e) {
			throw new ToolException(StringUtil.format("get cloumn count failure,the reason is: {}", e.getMessage()), e);
		}

		int[] columnToProperty = new int[cols + 1];

		Arrays.fill(columnToProperty, -1);

		for (int col = 1; col <= cols; col++) {
			String columnName;
			try {
				columnName = rsmd.getColumnLabel(col);
			} catch (SQLException e) {
				throw new ToolException(StringUtil.format("get cloumn name failure,the reason is: {}", e.getMessage()),
						e);
			}

			for (int i = 0; i < props.length; i++) {

				if (columnName.equalsIgnoreCase(props[i].getName())) {
					columnToProperty[col] = i;
					break;
				}
			}
		}
		return columnToProperty;
	}

	/**
	 * 创建对象
	 */
	private <T> T createBean(final ResultSet resultSet, final Class<T> type, final int[] columnToProperty,
			final PropertyDescriptor... props) {
		final T instance = ClassUtil.newInstance(type);

		for (int i = 1; i < columnToProperty.length; i++) {
			if (columnToProperty[i] == -1) {
				continue;
			}

			final PropertyDescriptor prop = props[columnToProperty[i]];

			final Class<?> propType = prop.getPropertyType();

			Object value;
			try {
				value = resultSet.getObject(i);
			} catch (SQLException e) {
				throw new ToolException(StringUtil
						.format("resultset convert to bean instance failure,the reaons is: {}", e.getMessage()), e);
			}

			if (Objects.isNull(value) && propType.isPrimitive()) {
				if (Number.class.isAssignableFrom(propType)) {
					value = 0;
				} else if (Character.class.isAssignableFrom(propType)) {
					value = Character.valueOf((char) 0);
				} else {
					value = false;
				}
			}

			BeanUtils.setSimpleProperty(instance, prop.getName(), value, null);

		}

		return instance;
	}
}
