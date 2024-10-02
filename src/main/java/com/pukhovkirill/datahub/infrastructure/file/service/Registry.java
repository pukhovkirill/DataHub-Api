package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;

public interface Registry {
    void register(String key, StorageGateway gateway);
    void release(String key);
}
