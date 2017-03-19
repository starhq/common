package com.star.template.db.model;

import java.io.Serializable;
import java.util.List;

/**
 * 表
 * <p>
 * Created by win7 on 2017/2/19.
 */
public class Table implements Serializable {

    /**
     * 表sql名
     */
    private String sqlName;

    /**
     * 注释
     */
    private String remarks;

    /**
     * 类名
     */
    private String className;

    /**
     * 标量名，和类名区别首字母小写
     */
    private String variableName;

    /**
     * 列
     */
    private List<Column> columns;

    /**
     * jpa用，1对多
     */
    private List<ForeignKey> oneToMany;

    /**
     * jpa用，多对1
     */
    private List<ForeignKey> manyToOne;


    public Table() {
    }

    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<ForeignKey> getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(List<ForeignKey> oneToMany) {
        this.oneToMany = oneToMany;
    }

    public List<ForeignKey> getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(List<ForeignKey> manyToOne) {
        this.manyToOne = manyToOne;
    }

}
