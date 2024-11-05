package com.pukhovkirill.datahub.infrastructure.gateway.service;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;

import java.util.Collection;

public interface OngoingGatewayService {
    void register(String key, StorageGateway gateway);
    void release(String key);
    Collection<String> list();
    StorageGateway get(String key);
}
