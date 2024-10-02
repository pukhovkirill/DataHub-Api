package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
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
            throw new RuntimeException("Storage gateway already registered");
    }

    @Override
    public void release(String key) {
        if(storageGatewayCache.containsKey(key)){
            storageGatewayCache.remove(key);
        }else
            throw new RuntimeException("Storage gateway not registered");
    }

    @Override
    public StorageGateway get(String key) {
        if(storageGatewayCache.containsKey(key)){
            return storageGatewayCache.get(key);
        }else
            throw new RuntimeException("Storage gateway not registered");
    }
}