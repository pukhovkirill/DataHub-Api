package com.pukhovkirill.datahub.infrastructure.gateway.exception;

public class FailedToServerConnectException extends RuntimeException {

    private final String protocol;

    public FailedToServerConnectException(String protocol) {
        super(protocol);
        this.protocol = protocol;
    }

    public FailedToServerConnectException(String message, String protocol) {
        super(message);
        this.protocol = protocol;
    }

    public FailedToServerConnectException(String message, String protocol, Throwable cause) {
        super(message, cause);
        this.protocol = protocol;
    }

    public FailedToServerConnectException(Throwable cause, String protocol) {
        super(cause);
        this.protocol = protocol;
    }
}
