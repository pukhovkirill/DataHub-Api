package com.pukhovkirill.datahub.entity.exception;

public class StorageEntityNotFoundException extends Exception{
    public StorageEntityNotFoundException(String filename) {
        super(String.format("Could not find storage entity with name '%s'", filename));
    }
}
