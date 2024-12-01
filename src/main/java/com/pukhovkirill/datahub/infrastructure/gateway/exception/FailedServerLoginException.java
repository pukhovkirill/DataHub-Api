package com.pukhovkirill.datahub.infrastructure.gateway.exception;

public class FailedServerLoginException extends RuntimeException{

    private final String protocol;

    public FailedServerLoginException(String protocol) {
        super(protocol);
        this.protocol = protocol;
    }

    public FailedServerLoginException(String message, String protocol) {
        super(message);
        this.protocol = protocol;
    }

    public FailedServerLoginException(String message, String protocol, Throwable cause) {
        super(message, cause);
        this.protocol = protocol;
    }

    public FailedServerLoginException(Throwable cause, String protocol) {
        super(cause);
        this.protocol = protocol;
    }

}
