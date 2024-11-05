package com.pukhovkirill.datahub.infrastructure.gateway.service;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;

public class OngoingGatewayServiceImpl implements OngoingGatewayService {

    private final static ConcurrentMap<String, StorageGateway> storageGatewayCache;

    static{
        storageGatewayCache = new ConcurrentHashMap<>();
    }

    public OngoingGatewayServiceImpl(StorageGateway internalStorageGateway){
        if(!storageGatewayCache.containsKey("internal"))
            storageGatewayCache.put("internal", internalStorageGateway);
    }

    @Override
    public void register(String key, StorageGateway gateway) {
        if(!storageGatewayCache.containsKey(key)){
            storageGatewayCache.put(key, gateway);
        }else
            throw new StorageGatewayAlreadyExistsException(key);
    }

    @Override
    public void release(String key) throws IOException {
        if(storageGatewayCache.containsKey(key)){
            if(storageGatewayCache.get(key) instanceof Closeable gateway)
                gateway.close();
            storageGatewayCache.remove(key);
        }else
            throw new StorageGatewayNotFoundException(key);
    }

    @Override
    public Collection<String> list() {
        return new ArrayList<>(storageGatewayCache.keySet());
    }

    @Override
    public StorageGateway get(String key) {
        if(storageGatewayCache.containsKey(key)){
            return storageGatewayCache.get(key);
        }else
            throw new StorageEntityNotFoundException(key);
    }
}
