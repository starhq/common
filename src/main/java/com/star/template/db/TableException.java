package com.star.template.db;

/**
 * Created by win7 on 2017/2/25.
 */
public class TableException extends RuntimeException {

    private static final long serialVersionUID = 6440749361675575572L;

    public TableException() {
        super();
    }

    public TableException(String message) {
        super(message);
    }

    public TableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableException(Throwable cause) {
        super(cause);
    }

    protected TableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
