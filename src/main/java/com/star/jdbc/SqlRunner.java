package com.star.jdbc;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * sql执行器
 * 
 * @author starhq
 *
 */
public class SqlRunner {

	/**
	 * 查询
	 * 
	 * 注意connetion没关(方便在方法体外控制提交回滚等)
	 */
	public <T> T query(final Connection conn, final String sql, final RsHandler<T> rsh, final Object... params) {
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = conn.prepareStatement(sql);
			fillParams(stmt, params);
			resultSet = stmt.executeQuery();
			return rsh.handle(resultSet);
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("run query sql: {} failure,the reason is: {}", sql, e.getMessage()), e);
		} finally {
			ConnectionManager.close(resultSet, stmt, null);
		}
	}

	/**
	 * 插入更新删除
	 * 
	 * 注意connetion没关(方便在方法体外控制提交回滚等)
	 */
	public int update(final Connection conn, final String sql, final Object... params) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			fillParams(stmt, params);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("run update sql: {} failure,the reason is: {}", sql, e.getMessage()), e);
		} finally {
			ConnectionManager.closeStatement(stmt);
		}
	}

	/**
	 * 批量插入更新删除
	 */
	public int[] updateBatch(final Connection conn, final String sql, final Object[]... paramsBatch) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			for (final Object[] params : paramsBatch) {
				fillParams(stmt, params);
				stmt.addBatch();
			}
			return stmt.executeBatch();
		} catch (SQLException e) {
			throw new ToolException(
					StringUtil.format("run batch update sql: {} failure,the reason is: {}", sql, e.getMessage()), e);
		} finally {
			ConnectionManager.closeStatement(stmt);
		}
	}

	/**
	 * 给PreparedStatement设置参数
	 */
	private void fillParams(final PreparedStatement stmt, final Object... params) {
		if (ArrayUtil.isEmpty(params)) {
			return;
		}

		for (int i = 1; i <= params.length; i++) {
			final Object parameter = params[i - 1];
			if (Objects.isNull(parameter)) {
				int sqlType;
				try {
					final ParameterMetaData pmd = stmt.getParameterMetaData();
					sqlType = pmd.getParameterType(i);
				} catch (SQLException e) {
					sqlType = Types.VARCHAR;
				}
				try {
					stmt.setNull(i, sqlType);
				} catch (SQLException e) {
					throw new ToolException(StringUtil
							.format("preparedStatement set parameter failue,the reason is: {}", e.getMessage()), e);
				}
			} else {
				try {
					stmt.setObject(i, parameter);
				} catch (SQLException e) {
					throw new ToolException(StringUtil
							.format("preparedStatement set parameter failue,the reason is: {}", e.getMessage()), e);
				}
			}
		}

	}
}
