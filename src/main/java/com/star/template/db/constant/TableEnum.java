package com.star.template.db.constant;

/**
 * Created by win7 on 2017/2/25.
 */
public enum TableEnum {

    CAT("TABLE_CAT", "表类别"),
    SCHEM("TABLE_SCHEM", "表模式"),
    NAME("TABLE_NAME", "表名称"),
    TYPE("TABLE_TYPE", "表类别"),
    REMARKS("REMARKS", "注释"),
    SYNONYM("SYNONYM", "同义词"),
    PKTABLE("PKTABLE_NAME", "主表名"),
    PKCOLUMN("PKCOLUMN_NAME", "关联主表的列名"),
    FKTABLE("FKTABLE_NAME", "从表名"),
    FKCOLUMN("FKCOLUMN_NAME", "关联从表的列名");

    public final String code;
    public final String desc;

    TableEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
