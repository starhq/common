package com.star.template.db.util;

import com.star.template.db.model.Column;

/**
 * Created by win7 on 2017/3/5.
 */
public final class ColumnUtil {

    private ColumnUtil() {
    }

    /**
     * 得到JSR303 bean validation的验证表达式
     */
    public static String getHibernateValidatorExpression(Column column) {
        if (!column.getIsPK() && !column.getIsNullable()) {
            if (TypeUtil.isString(column.getJavaType())) {
                return "@NotBlank " + getNotRequiredHibernateValidatorExpression(column);
            } else {
                return "@NotNull " + getNotRequiredHibernateValidatorExpression(column);
            }
        } else {
            return getNotRequiredHibernateValidatorExpression(column);
        }
    }

    public static String getNotRequiredHibernateValidatorExpression(Column column) {
        String result = "";
        if (column.getSqlName().indexOf("mail") >= 0) {
            result += "@Email ";
        }
        if (TypeUtil.isString(column.getJavaType()) && column.getSize() > 0) {
            result += String.format("@Length(max=%s)", column.getSize());
        }
        if (TypeUtil.isIntegerNumber(column.getJavaType())) {
            if (column.getJavaType().endsWith("short")) {
                result += " @Max(" + Short.MAX_VALUE + ")";
            } else if (column.getJavaType().endsWith("byte")) {
                result += " @Max(" + Byte.MAX_VALUE + ")";
            }
        }
        return result.trim();
    }
}
