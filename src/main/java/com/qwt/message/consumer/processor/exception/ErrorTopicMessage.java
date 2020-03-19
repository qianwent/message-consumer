package com.qwt.message.consumer.processor.exception;

public class ErrorTopicMessage {

    public String message;
    public String exceptionMessage;

    public ErrorTopicMessage(String message, String exceptionMessage) {
        this.message = message;
        this.exceptionMessage = exceptionMessage;
    }
}
