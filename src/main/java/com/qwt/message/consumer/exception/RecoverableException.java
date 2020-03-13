package com.qwt.message.consumer.exception;

public class RecoverableException extends RuntimeException {

    private String value;

    public RecoverableException() {
    }

    public RecoverableException(String message) {
        super(message);
    }

    public RecoverableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecoverableException(String message, String value, Throwable cause) {
        super(message, cause);
        this.value = value;
    }

    public RecoverableException(Throwable cause) {
        super(cause);
    }

    public RecoverableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
