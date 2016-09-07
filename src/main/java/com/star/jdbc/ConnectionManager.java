/**
 * 
 */
package com.star.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 数据库连接管理类
 * 
 * @author starhq
 *
 */
public final class ConnectionManager {

	/**
	 * 
	 */
	private ConnectionManager() {
	}

	/**
	 * 根据传入的url动态获取连接
	 * 
	 * 暂支持mysql,oracle,sqlserver
	 */
	public static Connection getConnection(final String url, final String user, final String password) {
		Assert.isTrue(!StringUtil.isBlank(url) && !StringUtil.isBlank(user),
				"get connection failure,the input url or user is null");
		String driverManager;
		if (url.indexOf("mysql") >= 0) {
			driverManager = "com.mysql.jdbc.Driver";
		} else if (url.indexOf("oracle") >= 0) {
			driverManager = "oracle.jdbc.driver.OracleDriver";
		} else {
			driverManager = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}

		try {
			Class.forName(driverManager);
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ToolException(StringUtil.format("get connection failue,the reason is: {}", e.getMessage()), e);
		}

	}

	/**
	 * 关闭连接
	 * 
	 */
	public static void closeConnection(final Connection conn) {
		if (!Objects.isNull(conn)) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new ToolException(StringUtil.format("close connection failure,the reason is: {}", e.getMessage()),
						e);
			}
		}
	}

	/**
	 * 关闭resultset
	 * 
	 */
	public static void closeResultSet(final ResultSet resultSet) {
		if (!Objects.isNull(resultSet)) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				throw new ToolException(StringUtil.format("close resultset failure,the reason is: {}", e.getMessage()),
						e);
			}
		}
	}

	/**
	 * 关闭Statement
	 * 
	 */
	public static void closeStatement(final Statement statement) {
		if (!Objects.isNull(statement)) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new ToolException(StringUtil.format("close statment failue,the reason is: {}", e.getMessage()),
						e);
			}
		}
	}

	/**
	 * 提交事务并关闭
	 */
	public static void commitAndClose(final Connection conn) {
		if (!Objects.isNull(conn)) {
			try {
				try {
					conn.commit();
				} catch (SQLException e) {
					throw new ToolException(
							StringUtil.format("commit transaction failue,the reason is: {}", e.getMessage()), e);
				}
			} finally {
				closeConnection(conn);
			}
		}
	}

	/**
	 * 回滚事务并关闭
	 */
	public static void rollbackAndClose(final Connection conn) {
		if (!Objects.isNull(conn)) {
			try {
				try {
					conn.rollback();
				} catch (SQLException e) {
					throw new ToolException(
							StringUtil.format("rollback transaction failue,the reason is: {}", e.getMessage()), e);
				}
			} finally {
				closeConnection(conn);
			}
		}
	}

	/**
	 * 简化关闭
	 */
	public static void close(final ResultSet resultSet, final Statement statement, final Connection conn) {
		try {
			closeResultSet(resultSet);
		} finally {
			try {
				closeStatement(statement);
			} finally {
				closeConnection(conn);
			}
		}
	}

}
