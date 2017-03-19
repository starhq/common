package com.star.template.db.util;

import com.star.string.StringUtil;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换工具
 * <p>
 * Created by win7 on 2017/2/26.
 */
public final class TypeUtil {

    /**
     * jdbc type和java type类型的映射
     */
    public static final Map<Integer, String> SQLTOJAVA = new HashMap<>();

    static {
        SQLTOJAVA.put(Types.TINYINT, "java.lang.Byte");
        SQLTOJAVA.put(Types.SMALLINT, "java.lang.Short");
        SQLTOJAVA.put(Types.INTEGER, "java.lang.Integer");
        SQLTOJAVA.put(Types.BIGINT, "java.lang.Long");
        SQLTOJAVA.put(Types.REAL, "java.lang.Float");
        SQLTOJAVA.put(Types.FLOAT, "java.lang.Double");
        SQLTOJAVA.put(Types.DOUBLE, "java.lang.Double");
        SQLTOJAVA.put(Types.DECIMAL, "java.math.BigDecimal");
        SQLTOJAVA.put(Types.NUMERIC, "java.math.BigDecimal");
        SQLTOJAVA.put(Types.BIT, "java.lang.Boolean");
        SQLTOJAVA.put(Types.BOOLEAN, "java.lang.Boolean");
        SQLTOJAVA.put(Types.CHAR, "java.lang.String");
        SQLTOJAVA.put(Types.VARCHAR, "java.lang.String");
        SQLTOJAVA.put(Types.LONGVARCHAR, "java.lang.String");
        SQLTOJAVA.put(Types.BINARY, "byte[]");
        SQLTOJAVA.put(Types.VARBINARY, "byte[]");
        SQLTOJAVA.put(Types.LONGVARBINARY, "byte[]");
        SQLTOJAVA.put(Types.DATE, "java.sql.Date");
        SQLTOJAVA.put(Types.TIME, "java.sql.Time");
        SQLTOJAVA.put(Types.TIMESTAMP, "java.sql.Timestamp");
        SQLTOJAVA.put(Types.CLOB, "java.sql.Clob");
        SQLTOJAVA.put(Types.BLOB, "java.sql.Blob");
        SQLTOJAVA.put(Types.ARRAY, "java.sql.Array");
        SQLTOJAVA.put(Types.REF, "java.sql.Ref");
        SQLTOJAVA.put(Types.STRUCT, "java.lang.Object");
        SQLTOJAVA.put(Types.JAVA_OBJECT, "java.lang.Object");
    }

    private TypeUtil() {
    }

    /**
     * 根据jdbctype获得java type
     *
     * @param sqlType       jdbctype
     * @param size          字段大小
     * @param decimalDigits 字段精度
     * @return javatype
     */
    public static String getJavaTypeBySqlType(final int sqlType, final int size, final int decimalDigits) {
        String result;
        switch (sqlType) {
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.TINYINT:
                if (size == 1 && decimalDigits == 0) {
                    result = "java.lang.Boolean";
                } else if (size < 3 && decimalDigits == 0) {
                    result = "java.lang.Byte";
                } else if (size < 5 && decimalDigits == 0) {
                    result = "java.lang.Short";
                } else if (size < 10 && decimalDigits == 0) {
                    result = "java.lang.Integer";
                } else if (size < 19 && decimalDigits == 0) {
                    result = "java.lang.Long";
                } else {
                    result = "java.math.BigDecimal";
                }
                break;
            default:
                result = SQLTOJAVA.get(sqlType);
                break;
        }
        return StringUtil.isBlank(result) ? "java.lang.Object" : result;
    }

    /**
     * 是否是string类型
     *
     * @param javaType java类型
     * @return 是否
     */
    public static boolean isString(final String javaType) {
        return javaType.endsWith("String");

    }

    /**
     * 是否integer
     *
     * @param javaType java类型
     * @return 是否
     */
    public static boolean isIntegerNumber(final String javaType) {
        return javaType.endsWith("Long") || javaType.endsWith("Integer") || javaType.endsWith("Short") || javaType
                .endsWith("Byte") || javaType.endsWith("long") || javaType.endsWith("int") || javaType.endsWith
                ("short") || javaType.endsWith("byte");
    }
}
