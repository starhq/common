package com.star.template.db.util;

import com.star.config.Config;
import com.star.string.StringUtil;
import com.star.template.db.constant.ConfigEnum;
import com.star.template.db.constant.ConnectionEnum;

/**
 * 风向下读取属性
 * <p>
 * Created by win7 on 2017/3/10.
 */
public final class PropertyUtil {

    private PropertyUtil() {
    }

    /**
     * 从属性文件中读取catalog
     *
     * @return catalog，如果没有返回空字符串
     */
    public static String getCatalog() {
        return Config.getString(ConfigEnum.JDBC_CATALOG.code, StringUtil.EMPTY);
    }

    /**
     * 从属性文件读取schema
     *
     * @return schema，如果没有返回空字符串
     */
    public static String getSchema() {
        return Config.getString(ConfigEnum.JDBC_SCHEMA.code, StringUtil.EMPTY);
    }

    /**
     * 数据库连接的用户名
     *
     * @return username 数据库用户名
     */
    public static String getUser() {
        return Config.getString(ConnectionEnum.USERNAME.code, "starhq");
    }

    /**
     * 数据库连接的用户名
     *
     * @return password 数据库密码
     */
    public static String getPassword() {
        return Config.getString(ConnectionEnum.PASSWORD.code, "12345678");
    }

    /**
     * 数据库连接的地址
     *
     * @return url 数据库地址
     */
    public static String getUrl() {
        return Config.getString(ConnectionEnum.URL.code, StringUtil.EMPTY);
    }

    /**
     * 数据库连接的地址
     *
     * @return prefix 前缀
     */
    public static String getPrefixes() {
        return Config.getString(ConfigEnum.TABLE_REMOVE_PREFIXES.code, StringUtil.EMPTY);
    }

    /**
     * 获得模板目录
     *
     * @return template 模板地址
     */
    public static String getTemplatePath() {
        return Config.getString(ConfigEnum.TEMPLATE.code, StringUtil.EMPTY);
    }

    /**
     * 获得输出目录
     *
     * @return template 模板地址
     */
    public static String getOutPath() {
        return Config.getString(ConfigEnum.OUT.code, StringUtil.EMPTY);
    }

    /**
     * 获得基本包
     *
     * @return template 模板地址
     */
    public static String getPackage() {
        return Config.getString(ConfigEnum.PACKAGE.code, StringUtil.EMPTY);
    }
}
