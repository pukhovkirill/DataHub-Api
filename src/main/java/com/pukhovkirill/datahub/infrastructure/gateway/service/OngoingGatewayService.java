package com.pukhovkirill.datahub.infrastructure.gateway.service;

import java.io.IOException;
import java.util.Collection;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;

public interface OngoingGatewayService {
    void register(String key, StorageGateway gateway);
    void release(String key) throws IOException;
    Collection<String> list();
    StorageGateway get(String key);
}
