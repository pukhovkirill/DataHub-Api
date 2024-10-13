package com.pukhovkirill.datahub.infrastructure.exception;

public class FTPFileNotFoundException extends RuntimeException {

    public FTPFileNotFoundException(String filename) {
        super(String.format("Could not find file with name '%s'", filename));
    }

    public FTPFileNotFoundException(String filename, Throwable cause) {
        super(String.format("Could not find file with name '%s'", filename), cause);
    }

    public FTPFileNotFoundException(Throwable cause) {
        super(cause);
    }

}
