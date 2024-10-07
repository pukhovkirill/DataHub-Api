package com.pukhovkirill.datahub.entity.exception;

public class StorageGatewayAlreadyExistsException extends RuntimeException{
    public StorageGatewayAlreadyExistsException(String name) {
        super(String.format("Storage gateway with the name '%s' already exists", name));
    }

    public StorageGatewayAlreadyExistsException(String name, Throwable cause) {
        super(String.format("Storage gateway with the name '%s' already exists", name), cause);
    }

    public StorageGatewayAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
