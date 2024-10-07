package com.pukhovkirill.datahub.entity.exception;

public class StorageGatewayNotFoundException extends RuntimeException {
    public StorageGatewayNotFoundException(String name) {
        super(String.format("Could not find storage gateway with name '%s'", name));
    }

    public StorageGatewayNotFoundException(String name, Throwable cause) {
        super(String.format("Could not find storage gateway with name '%s'", name), cause);
    }

    public StorageGatewayNotFoundException(Throwable cause) {
        super(cause);
    }
}
