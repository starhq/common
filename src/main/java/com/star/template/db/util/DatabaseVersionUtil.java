package com.star.template.db.util;

import com.star.string.StringUtil;
import com.star.template.db.TableException;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 判断数据库版本
 * <p>
 * Created by win7 on 2017/3/11.
 */
public final class DatabaseVersionUtil {

    /**
     * oracle常量
     */
    public static final String ORACLE = "Oracle";
    /**
     * mysql常量
     */
    public static final String MYSQL = "MySQL";
    /**
     * sql server常量
     */
    public static final String MSSQL = "Microsoft SQL Server";
    /**
     * Postgre常量
     */
    public static final String POSTGRE = "PostgreSQL";
    /**
     * h2常量
     */
    public static final String H2DB = "H2";
    /**
     * hsql常量
     */
    public static final String HSQL = "HSQL";


    private DatabaseVersionUtil() {
    }

    /**
     * 是否oracle
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isOracle(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return ORACLE.equals(name);
    }

    /**
     * 是否hsql
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isHsql(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return HSQL.equals(name);
    }

    /**
     * 是否mysql
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isMysql(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return MYSQL.equals(name);
    }

    /**
     * 是否mssql
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isSqlServer(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return MSSQL.equals(name);
    }

    /**
     * 是否Postgre
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isPostgre(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return POSTGRE.equals(name);
    }

    /**
     * 是否Postgre
     *
     * @param metadata 元数据
     * @return 是否
     */
    public static boolean isH2(final DatabaseMetaData metadata) {
        final String name = getDatabaseName(metadata);
        return H2DB.equals(name);
    }

    /**
     * 查询数据库名
     *
     * @param metadata 元数据
     * @return 数据库名
     */
    private static String getDatabaseName(final DatabaseMetaData metadata) {
        try {
            return metadata.getDatabaseProductName();
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("determine wheather database is oracle is failure: {}", e
                    .getMessage()), e);
        }
    }
}
