package com.pukhovkirill.datahub.infrastructure.file.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PathParamException extends RuntimeException{

    @Getter
    private HttpStatus status;

    public PathParamException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    public PathParamException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.status = httpStatus;
    }

}
