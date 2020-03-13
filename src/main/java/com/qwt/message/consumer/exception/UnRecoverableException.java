package com.qwt.message.consumer.exception;

public class UnRecoverableException extends RuntimeException {

    private String value;

    public UnRecoverableException() {
    }

    public UnRecoverableException(String message) {
        super(message);
    }

    public UnRecoverableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnRecoverableException(String message, String value, Throwable cause) {
        super(message, cause);
        this.value = value;
    }

    public UnRecoverableException(Throwable cause) {
        super(cause);
    }

    public UnRecoverableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
