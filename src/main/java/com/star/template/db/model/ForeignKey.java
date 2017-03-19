package com.star.template.db.model;

import java.io.Serializable;

/**
 * 外键
 * <p>
 * Created by win7 on 2017/2/19.
 */
public class ForeignKey implements Serializable {

    private static final long serialVersionUID = 7251220914278167636L;

    /**
     * 关联的主表名
     */
    private String pkTable;

    private String pkTableClass;

    private String pkTableClassLower;

    /**
     * 关联主表的列名
     */
    private String pkColumn;

    private String pkCloumnClass;

    private String pkColumnLower;

    /**
     * 关联的从表名
     */
    private String fkTable;

    private String fkTableClass;

    private String fkTableLower;

    /**
     * 关联从表的列名
     */
    private String fkColumn;

    private String fkColumnClass;

    private String fkColumnLower;


    public ForeignKey() {
    }


    public String getPkTable() {
        return pkTable;
    }

    public void setPkTable(String pkTable) {
        this.pkTable = pkTable;
    }

    public String getPkTableClass() {
        return pkTableClass;
    }

    public void setPkTableClass(String pkTableClass) {
        this.pkTableClass = pkTableClass;
    }

    public String getPkTableClassLower() {
        return pkTableClassLower;
    }

    public void setPkTableClassLower(String pkTableClassLower) {
        this.pkTableClassLower = pkTableClassLower;
    }

    public String getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(String pkColumn) {
        this.pkColumn = pkColumn;
    }

    public String getPkCloumnClass() {
        return pkCloumnClass;
    }

    public void setPkCloumnClass(String pkCloumnClass) {
        this.pkCloumnClass = pkCloumnClass;
    }

    public String getPkColumnLower() {
        return pkColumnLower;
    }

    public void setPkColumnLower(String pkColumnLower) {
        this.pkColumnLower = pkColumnLower;
    }

    public String getFkTable() {
        return fkTable;
    }

    public void setFkTable(String fkTable) {
        this.fkTable = fkTable;
    }

    public String getFkTableClass() {
        return fkTableClass;
    }

    public void setFkTableClass(String fkTableClass) {
        this.fkTableClass = fkTableClass;
    }

    public String getFkTableLower() {
        return fkTableLower;
    }

    public void setFkTableLower(String fkTableLower) {
        this.fkTableLower = fkTableLower;
    }

    public String getFkColumn() {
        return fkColumn;
    }

    public void setFkColumn(String fkColumn) {
        this.fkColumn = fkColumn;
    }

    public String getFkColumnClass() {
        return fkColumnClass;
    }

    public void setFkColumnClass(String fkColumnClass) {
        this.fkColumnClass = fkColumnClass;
    }

    public String getFkColumnLower() {
        return fkColumnLower;
    }

    public void setFkColumnLower(String fkColumnLower) {
        this.fkColumnLower = fkColumnLower;
    }
}
