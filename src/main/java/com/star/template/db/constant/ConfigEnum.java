package com.star.template.db.constant;

/**
 * Created by win7 on 2017/2/25.
 */
public enum ConfigEnum {

    JDBC_SCHEMA("jdbc.schema", null, "数据源:schema"),
    JDBC_CATALOG("jdbc.catalog", null, "数据源:catalog"),
    TABLE_REMOVE_PREFIXES("tableRemovePrefixes", null, "需要移除的表名前缀,示例值: t_,v_"),
    TEMPLATE("templatepath", null, "模板目录"),
    OUT("outpath", null, "输出目录"),
    PACKAGE("basepackage", null, "基本包");

    public final String code;
    public final String defaultValue;
    public final String desc;

    ConfigEnum(String code, String defaultValue, String desc) {
        this.code = code;
        this.defaultValue = defaultValue;
        this.desc = desc;
    }
}
