package com.star.template.db.model;

import java.io.Serializable;

/**
 * 列
 * <p>
 * Created by win7 on 2017/2/19.
 */
public class Column implements Serializable {


    /**
     * sql类型名
     */
    private String sqlTypeName;

    /**
     * sql名
     */
    private String sqlName;


    /**
     * 是否主键
     */
    private Boolean isPK;


    /**
     * 大小
     */
    private Integer size;

    /**
     * 精度
     */
    private Integer decimalDigits;

    /**
     * 能否为空
     */
    private Boolean isNullable;

    /**
     * 是否索引
     */
    private Boolean isIndexed;

    /**
     * 是否唯一
     */
    private Boolean isUnique;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 注解
     */
    private String remarks;

    /**
     * java类型
     */
    private String javaType;

    /**
     * 列别名
     */
    private String columnAlias;

    /**
     * 列名,首字母大写，做set，get等方法的后缀用
     */
    private String columnName;


    /**
     * 首字母小写,做filed用
     */
    private String columnNameLower;


    /**
     * jsr验证表达式
     */
    private String hibernateValidatorExprssion;

    public Column() {
    }


    public String getSqlTypeName() {
        return sqlTypeName;
    }

    public void setSqlTypeName(String sqlTypeName) {
        this.sqlTypeName = sqlTypeName;
    }

    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public Boolean getIsPK() {
        return isPK;
    }

    public void setIsPK(Boolean isPK) {
        this.isPK = isPK;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public Boolean getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public Boolean getIsIndexed() {
        return isIndexed;
    }

    public void setIsIndexed(Boolean isIndexed) {
        this.isIndexed = isIndexed;
    }

    public Boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(Boolean isUnique) {
        this.isUnique = isUnique;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getHibernateValidatorExprssion() {
        return hibernateValidatorExprssion;
    }

    public void setHibernateValidatorExprssion(String hibernateValidatorExprssion) {
        this.hibernateValidatorExprssion = hibernateValidatorExprssion;
    }

    public String getColumnNameLower() {
        return columnNameLower;
    }

    public void setColumnNameLower(String columnNameLower) {
        this.columnNameLower = columnNameLower;
    }
}
