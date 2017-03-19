package com.star.template.db.constant;

/**
 * 连接常量
 * <p>
 * Created by win7 on 2017/3/11.
 */
public enum ConnectionEnum {

    USERNAME("jdbc.username", "数据源:用户名"),
    PASSWORD("jdbc.password", "数据源:密码"),
    URL("jdbc.url", "数据源:URL");

    public final String code;
    public final String desc;

    ConnectionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
