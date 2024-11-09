package com.pukhovkirill.datahub.infrastructure.file.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class FileParamException extends RuntimeException{

    @Getter
    private HttpStatus status;

    public FileParamException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    public FileParamException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.status = httpStatus;
    }

}
