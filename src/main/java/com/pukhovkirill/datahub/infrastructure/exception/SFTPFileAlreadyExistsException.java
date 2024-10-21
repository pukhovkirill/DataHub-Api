package com.pukhovkirill.datahub.infrastructure.exception;

public class SFTPFileAlreadyExistsException extends RuntimeException {
    public SFTPFileAlreadyExistsException(String filename) {
        super(String.format("File with the name '%s' already exists", filename));
    }

    public SFTPFileAlreadyExistsException(String filename, Throwable cause) {
        super(String.format("File with the name '%s' already exists", filename), cause);
    }

    public SFTPFileAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
