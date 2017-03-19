package com.star.template.db.table;

import com.star.collection.CollectionUtil;
import com.star.config.Config;
import com.star.jdbc.SqlRunner;
import com.star.jdbc.StringHandler;
import com.star.string.StringUtil;
import com.star.template.db.TableException;
import com.star.template.db.constant.ColumnEnum;
import com.star.template.db.constant.Constant;
import com.star.template.db.constant.TableEnum;
import com.star.template.db.model.Column;
import com.star.template.db.model.ForeignKey;
import com.star.template.db.model.Table;
import com.star.template.db.util.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by win7 on 2017/2/25.
 */
public class Processor {

    /**
     * 根据结果记创建表信息
     *
     * @param resultSet 结果姐
     * @return 表信息
     */
    public Table create(final ResultSet resultSet) {
        try {
            final String tableName = resultSet.getString(TableEnum.NAME.code);
            final String remarks = getRemarks(tableName, resultSet);
            final Table table = new Table();
            table.setSqlName(tableName);
            table.setRemarks(remarks);

            final String name = StringUtil.toCamelCase(removePrefix(tableName));


            table.setClassName(StringUtil.upperOrLowerFirst(name, true));
            table.setVariableName(StringUtil.upperOrLowerFirst(name, false));
            final List<Column> columns = retriveTableColumns(tableName);
            table.setColumns(columns);

            final List<ForeignKey> importedKeys = initForeginKeys(tableName, true);
            table.setManyToOne(importedKeys);
            final List<ForeignKey> exportedKeys = initForeginKeys(tableName, false);
            table.setOneToMany(exportedKeys);
            return table;
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("create table failure,the reason is: {}", e.getMessage()), e);
        }
    }


    private List<ForeignKey> initForeginKeys(final String sqlName, final boolean isImported) {
        final DatabaseMetaData metaData = DatabaseUtil.getInstance().getMetaData();


        final List<ForeignKey> foreignKeys = new ArrayList<>();

        try (ResultSet resultSet = isImported ? metaData.getImportedKeys(PropertyUtil.getSchema(),
                PropertyUtil.getCatalog(), sqlName) : metaData.getExportedKeys(PropertyUtil.getSchema(),
                PropertyUtil.getCatalog(), sqlName)) {


            while (resultSet.next()) {
                final String pkTable = resultSet.getString(TableEnum.PKTABLE.code);
                final String pkColumn = resultSet.getString(TableEnum.PKCOLUMN.code);
                final String fkTable = resultSet.getString(TableEnum.FKTABLE.code);
                final String fkColumn = resultSet.getString(TableEnum.FKCOLUMN.code);

                final ForeignKey foreignKey = new ForeignKey();

                String name = StringUtil.toCamelCase(removePrefix(pkTable));

                foreignKey.setPkTable(pkTable);
                foreignKey.setPkTableClass(StringUtil.upperOrLowerFirst(name, true));
                foreignKey.setPkTableClassLower(StringUtil.upperOrLowerFirst(name, false));

                foreignKey.setPkColumn(pkColumn);
                foreignKey.setPkCloumnClass(StringUtil.upperOrLowerFirst(StringUtil.toCamelCase(pkColumn), true));
                foreignKey.setPkColumnLower(StringUtil.upperOrLowerFirst(StringUtil.toCamelCase(pkColumn), false));


                name = StringUtil.toCamelCase(removePrefix(fkTable));

                foreignKey.setFkTable(name);
                foreignKey.setFkTableClass(StringUtil.upperOrLowerFirst(name, true));
                foreignKey.setFkTableLower(StringUtil.upperOrLowerFirst(name, false));

                foreignKey.setFkColumn(fkColumn);
                foreignKey.setFkColumnClass(StringUtil.upperOrLowerFirst(StringUtil.toCamelCase(fkColumn), true));
                foreignKey.setFkColumnLower(StringUtil.upperOrLowerFirst(StringUtil.toCamelCase(fkColumn), true));


                foreignKeys.add(foreignKey);
            }

            return foreignKeys;
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("init table {}'s foreign key failure,the reason is" +
                    " {}", sqlName, e.getMessage()), e);
        }
    }


    /**
     * 获得所有表信息
     *
     * @return 表集合
     */
    public List<Table> getAllTables() {
        final DatabaseMetaData metaData = DatabaseUtil.getInstance().getMetaData();
        try (ResultSet resultSet = metaData.getTables(PropertyUtil.getCatalog(), PropertyUtil.getSchema(), null,
                null)) {
            final List<Table> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(create(resultSet));
            }
            return tables;
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("get all tables failure,the reason is: {}", e.getMessage()), e);
        }
    }

    private String getOracleTableComments(final String tableName) {
        String sql = "SELECT comments FROM user_tab_comments WHERE table_name= ?";
        final SqlRunner sqlRunner = new SqlRunner();
        return sqlRunner.query(DatabaseUtil.getInstance().getConnection(), sql, new StringHandler(), tableName);
    }

    private List<Column> retriveTableColumns(final String tableName) {
        final List<String> primaryKeys = getTablePrimaryKeys(tableName);

        //索引集合
        final List<String> indices = new ArrayList<>();
        //列名+索引名（id+primary）
        final Map<String, String> uniqueIndices = new HashMap<>();
        //索引名+列的集合（primary+id）
        final Map<String, List<String>> uniqueColumns = new HashMap<>();

        final DatabaseMetaData metaData = DatabaseUtil.getInstance().getMetaData();
        try (ResultSet indexRs = metaData.getIndexInfo(PropertyUtil.getSchema(),
                PropertyUtil.getCatalog(), tableName, false, true)) {


            while (indexRs.next()) {
                final String columnName = indexRs.getString(ColumnEnum.COLUMNNAME.code);
                if (!StringUtil.isBlank(columnName)) {
                    indices.add(columnName);
                }

                final String indexName = indexRs.getString(ColumnEnum.INDEX.code);
                final boolean nonUnique = indexRs.getBoolean(ColumnEnum.NONUNIQUE.code);

                if (!nonUnique && !StringUtil.isBlank(columnName) && !StringUtil.isBlank(indexName)) {
                    List<String> column = uniqueColumns.get(indexName);
                    if (CollectionUtil.isEmpty(column)) {
                        column = new ArrayList<>();
                        uniqueColumns.put(indexName, column);
                    }
                    column.add(columnName);
                    uniqueIndices.put(columnName, indexName);
                }
            }
            return getTableColumn(tableName, primaryKeys, indices, uniqueIndices, uniqueColumns);
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("get {}'s index failure: {}", tableName, e.getMessage
                    ()), e);
        }
    }

    private List<Column> getTableColumn(final String tableName, final List<String> primaryKeys, final List<String>
            indices, final Map<String, String> uniqueIndices, final Map<String, List<String>> uniqueColumns) {
        final DatabaseMetaData metaData = DatabaseUtil.getInstance().getMetaData();
        final List<Column> columns = new ArrayList<>();

        try (ResultSet columnRs = metaData.getColumns(PropertyUtil.getSchema(), PropertyUtil.getCatalog(), tableName,
                null)) {
            while (columnRs.next()) {
                final int sqlType = columnRs.getInt(ColumnEnum.DATATYPE.code);
                final String sqlTypeName = columnRs.getString(ColumnEnum.TYPENAME.code);
                final String sqlName = columnRs.getString(ColumnEnum.COLUMNNAME.code);
                final String defaultValue = columnRs.getString(ColumnEnum.COLUMNDEF.code);
                final String remarks = getRemarks(tableName, columnRs);

                final boolean isNullable = DatabaseMetaData.columnNullable == columnRs.getInt(ColumnEnum.NULLABLE.code);
                final int size = columnRs.getInt(ColumnEnum.COLUMNSIZE.code);
                final int decimalDigits = columnRs.getInt(ColumnEnum.DECIMALDIGITS.code);

                final boolean isPk = primaryKeys.contains(sqlName);
                final boolean isIndexed = indices.contains(sqlName);

                final String uniqueIndex = uniqueIndices.get(sqlName);
                List<String> uniqueList = null;
                if (!StringUtil.isBlank(uniqueIndex)) {
                    uniqueList = uniqueColumns.get(uniqueIndex);
                }

                final boolean isUnique = !CollectionUtil.isEmpty(uniqueList) && uniqueList.contains(sqlName);

                final Column column = new Column();
                column.setSqlName(sqlName);
                column.setSqlTypeName(sqlTypeName);
                column.setDefaultValue(defaultValue);
                column.setRemarks(remarks);
                column.setIsNullable(isNullable);
                column.setSize(size);
                column.setDecimalDigits(decimalDigits);
                column.setIsPK(isPk);
                column.setIsIndexed(isIndexed);
                column.setIsUnique(isUnique);

                final String sqlTypeToJavaTYpe = TypeUtil.getJavaTypeBySqlType(sqlType, size, decimalDigits);
                final String javaType = Config.getString(Constant.MAPPING + sqlTypeToJavaTYpe, sqlTypeToJavaTYpe);
                column.setJavaType(javaType);

                final String columnName = StringUtil.toCamelCase(sqlName);
                column.setColumnName(StringUtil.upperOrLowerFirst(columnName, true));
                column.setColumnNameLower(StringUtil.upperOrLowerFirst(columnName, false));

                final String columnAlias = StringUtil.defaultIfEmpty(column.getRemarks(), column.getColumnNameLower());

                final StringTokenizer stringTokenizer = new StringTokenizer(columnAlias, StringUtil.TABS + StringUtil
                        .CRLF + StringUtil.PAGEBREAKS);

                final StringJoiner joiner = new StringJoiner(StringUtil.SPACE);

                while (stringTokenizer.hasMoreElements()) {
                    joiner.add((String) stringTokenizer.nextElement());
                }

                column.setColumnAlias(joiner.toString());
                column.setHibernateValidatorExprssion(ColumnUtil.getHibernateValidatorExpression(column));

                columns.add(column);
            }
            return columns;
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("get {}'s column failure: {}", tableName, e.getMessage
                    ()), e);
        }
    }

    private String getRemarks(final String tableName, final ResultSet columnRs) throws SQLException {
        String remarks = columnRs.getString(ColumnEnum.REMARKS.code);
        if (StringUtil.isBlank(remarks) && DatabaseVersionUtil.isOracle(DatabaseUtil.getInstance()
                .getConnection().getMetaData())) {
            remarks = getOracleTableComments(tableName);
        }
        return remarks;
    }

    private List<String> getTablePrimaryKeys(final String tableName) {
        final List<String> primaryKeys = new ArrayList<>();
        try (ResultSet primaryKeyRs = DatabaseUtil.getInstance().getMetaData().getPrimaryKeys(PropertyUtil.getSchema(),
                PropertyUtil.getCatalog(), tableName)) {
            while (primaryKeyRs.next()) {
                final String columnName = primaryKeyRs.getString(ColumnEnum.COLUMNNAME.code);
                primaryKeys.add(columnName);
            }
            return primaryKeys;
        } catch (SQLException e) {
            throw new TableException(StringUtil.format("get {}'s primary keys failure: {}", tableName, e.getMessage()
            ), e);
        }

    }


    /**
     * 如果有表前缀，删除表铅锤
     *
     * @return
     */
    private String removePrefix(final String tableName) {
        final String prefixes = PropertyUtil.getPrefixes();

        String name = tableName;
        final String[] prefixArray = StringUtil.split(prefixes, StringUtil.COMMA);
        for (final String prefix : prefixArray) {
            name = StringUtil.removePrefix(tableName, prefix);
        }
        return name;
    }

}
