package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;

public interface OngoingGatewayService {

    void register(String key, StorageGateway gateway);
    void release(String key);
    StorageGateway get(String key);
}
