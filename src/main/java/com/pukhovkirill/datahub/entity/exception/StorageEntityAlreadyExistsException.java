package com.pukhovkirill.datahub.entity.exception;

public class StorageEntityAlreadyExistsException extends RuntimeException{

    public StorageEntityAlreadyExistsException(String filename) {
        super(String.format("Storage entity with the name '%s' already exists", filename));
    }

    public StorageEntityAlreadyExistsException(String filename, Throwable cause) {
        super(String.format("Storage entity with the name '%s' already exists", filename), cause);
    }

    public StorageEntityAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
