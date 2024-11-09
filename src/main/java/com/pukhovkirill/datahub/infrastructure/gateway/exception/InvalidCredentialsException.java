package com.pukhovkirill.datahub.infrastructure.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public InvalidCredentialsException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    public InvalidCredentialsException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.status = httpStatus;
    }

}
