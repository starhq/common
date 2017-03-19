package com.star.template.db.constant;

/**
 * Created by win7 on 2017/2/26.
 */
public enum ColumnEnum {

    COLUMNNAME("COLUMN_NAME", "列名"),
    INDEX("INDEX_NAME", "索引"),
    DATATYPE("DATA_TYPE", "数据类型"),
    TYPENAME("TYPE_NAME", "类型名称"),
    COLUMNDEF("COLUMN_DEF", "列默认值"),
    REMARKS("REMARKS", "列注解"),
    COLUMNSIZE("COLUMN_SIZE", "列长度"),
    DECIMALDIGITS("DECIMAL_DIGITS", "精度"),
    NULLABLE("NULLABLE", "为空"),
    NONUNIQUE("NON_UNIQUE", "是否唯一");


    public final String code;
    public final String desc;

    ColumnEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
