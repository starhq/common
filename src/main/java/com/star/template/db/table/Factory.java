package com.star.template.db.table;


import com.star.collection.CollectionUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;
import com.star.template.db.TableException;
import com.star.template.db.model.Table;
import com.star.template.db.util.DatabaseUtil;
import com.star.template.db.util.PropertyUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 表工厂，队processor的封装，方便调用
 * <p>
 * Created by win7 on 2017/3/11.
 */
public final class Factory {

    /**
     * 监听器
     */
    private final List<Listener> LISTENERS = new ArrayList<>();

    private Factory() {
    }

    /**
     * 持有factory实例
     */
    private static class FacotryHolder {
        /**
         * 初始化facotry
         */
        public static final Factory FACTORY = new Factory();
    }

    /**
     * 获得facotry实例
     *
     * @return facotry对象
     */
    public static Factory getInstance() {
        return FacotryHolder.FACTORY;
    }

    /**
     * 设置回掉监听
     *
     * @param listener 监听器
     */
    public void setListener(final Listener listener) {
        LISTENERS.add(listener);
    }

    /**
     * 设置回掉监听集合
     *
     * @param listeners 监听器集合
     */
    public void setListener(final List<Listener> listeners) {
        LISTENERS.addAll(listeners);
    }

    /**
     * 删除指定监听齐
     *
     * @param listener 监听器
     */
    public void removeListener(final Listener listener) {
        LISTENERS.remove(listener);
    }

    /**
     * 清空监听器
     */
    public void clearListener() {
        LISTENERS.clear();
    }

    /**
     * 获得所有表
     *
     * @return 所有表
     */
    public List<Table> getAllTables() {
        final Processor processor = new Processor();
        final List<Table> tables = processor.getAllTables();
        if (!CollectionUtil.isEmpty(LISTENERS)) {
            for (final Table table : tables) {
                for (final Listener listener : LISTENERS) {
                    listener.onCreated(table);
                }
            }
        }
        return tables;
    }

    /**
     * 按表名获取表
     *
     * @param tableName 表名
     * @return
     */
    public Table getTableByName(final String tableName) {
        Assert.isTrue(!StringUtil.isBlank(tableName), "table name can't be blank");
        final DatabaseMetaData metaData = DatabaseUtil.getInstance().getMetaData();
        Table table = null;
        try (ResultSet resultSet = metaData.getTables(PropertyUtil.getCatalog(), PropertyUtil.getSchema(), null,
                null)) {
            final Processor processor = new Processor();
            while (resultSet.next()) {
                table = processor.create(resultSet);
            }
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("get all tables failure,the reason is: {}", e.getMessage()), e);
        }
        if (!CollectionUtil.isEmpty(LISTENERS)) {
            for (final Listener listener : LISTENERS) {
                listener.onCreated(table);
            }
        }
        return table;
    }

    /**
     * 按表名数组获取表
     *
     * @param tableNames 表名数据
     * @return
     */
    public List<Table> getTableByNames(final String[] tableNames) {
        List<Table> tables = new ArrayList<>(tableNames.length);
        for (String tableName : tableNames) {
            tables.add(getTableByName(tableName));
        }
        return tables;
    }

    public static void main(String[] args) {
        List<Table> tables = Factory.getInstance().getAllTables();
        System.out.println(tables);
    }
}
