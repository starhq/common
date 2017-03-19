package com.star.jdbc;

import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by win7 on 2017/2/25.
 */
public class StringHandler implements RsHandler<String> {

    @Override
    public String handle(ResultSet resultSet) {
        try {
            return resultSet.next() ? resultSet.getString(1) : null;
        } catch (SQLException e) {
            throw new ToolException(StringUtil.format("get oracle comments failure: {}", e.getMessage()), e);
        }
    }
}
