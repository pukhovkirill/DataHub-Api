package com.pukhovkirill.datahub.infrastructure.exception;

public class SFTPFileNotFoundException extends RuntimeException {

    public SFTPFileNotFoundException(String filename) {
        super(String.format("Could not find file with name '%s'", filename));
    }

    public SFTPFileNotFoundException(String filename, Throwable cause) {
        super(String.format("Could not find file with name '%s'", filename), cause);
    }

    public SFTPFileNotFoundException(Throwable cause) {
        super(cause);
    }

}
