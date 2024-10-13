package com.pukhovkirill.datahub.infrastructure.exception;

public class FTPFileAlreadyExistsException extends RuntimeException{

    public FTPFileAlreadyExistsException(String filename) {
        super(String.format("File with the name '%s' already exists", filename));
    }

    public FTPFileAlreadyExistsException(String filename, Throwable cause) {
        super(String.format("File with the name '%s' already exists", filename), cause);
    }

    public FTPFileAlreadyExistsException(Throwable cause) {
        super(cause);
    }

}
