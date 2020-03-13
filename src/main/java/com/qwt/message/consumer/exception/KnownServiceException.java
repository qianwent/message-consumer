package com.qwt.message.consumer.exception;

/**
 * if KnownServiceException, no need to retry, simply write the message to error topic
 */
public class KnownServiceException extends RuntimeException {

    public KnownServiceException() {
    }

    public KnownServiceException(String s) {
        super(s);
    }

    public KnownServiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public KnownServiceException(Throwable throwable) {
        super(throwable);
    }

    public KnownServiceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
