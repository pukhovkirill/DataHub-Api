package com.pukhovkirill.datahub.entity.exception;

public class StorageEntityNotFoundException extends RuntimeException{
    public StorageEntityNotFoundException(String filename) {
        super(String.format("Could not find storage entity with name '%s'", filename));
    }

    public StorageEntityNotFoundException(String filename, Throwable cause) {
        super(String.format("Could not find storage entity with name '%s'", filename), cause);
    }

    public StorageEntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
