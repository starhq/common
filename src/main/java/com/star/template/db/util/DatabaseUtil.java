package com.star.template.db.util;

import com.star.jdbc.ConnectionManager;
import com.star.string.StringUtil;
import com.star.template.db.TableException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * connection，metadata的一些封装，方便使用,单例
 * <p>
 * Created by win7 on 2017/3/11.
 */
public final class DatabaseUtil {

    /**
     * 数据库连接
     */
    private Connection connection;

    /**
     * 数据库元数据
     */
    private DatabaseMetaData metaData;

    private DatabaseUtil() {
        try {
            if (Objects.isNull(connection) || connection.isClosed()) {
                final String url = PropertyUtil.getUrl();
                final String password = PropertyUtil.getPassword();
                final String user = PropertyUtil.getUser();
                if (url.indexOf("msyql") >= 0) {
                    connection = initConnection(url, user, password);
                } else {
                    connection = ConnectionManager.getConnection(url, user, password);
                }
            }
            if (Objects.isNull(metaData)) {
                metaData = connection.getMetaData();
            }
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("init connection and databasemetadata failure: {}", e
                    .getMessage()), e);
        }

    }


    /**
     * mysql连接要获得一些元数据要做额外配置，这里扩展下
     *
     * @return 数据库连接
     */
    private Connection initConnection(final String url, final String user, final String password) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            final Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            props.setProperty("remarks", "true");
            props.setProperty("useInformationSchema", "true");
            return DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException | SQLException e) {
            throw new TableException(StringUtil.format("get connection failure: {}", e.getMessage()), e);
        }

    }

    /**
     * 初始化
     */
    private static class DatabaseUtilHolder {
        /**
         * 初始化实例
         */
        public static final DatabaseUtil INSTANCE = new DatabaseUtil();
    }

    /**
     * 获得实例
     *
     * @return databaseutil实例
     */
    public static DatabaseUtil getInstance() {
        return DatabaseUtilHolder.INSTANCE;
    }

    /**
     * 获得连接
     *
     * @return 元数据
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 获得元数据
     *
     * @return 元数据
     */
    public DatabaseMetaData getMetaData() {
        return metaData;
    }
}
