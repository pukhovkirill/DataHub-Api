package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Scope("prototype")
public class CacheableStorageServiceImpl implements StorageService, Registry {

    private final static ConcurrentMap<String, StorageGateway> storageGatewayCache;

    static{
        storageGatewayCache = new ConcurrentHashMap<>();
    }

    private final StorageEntitiesCache entitiesCache;

    public CacheableStorageServiceImpl(@Qualifier("tireCacheService") StorageEntitiesCache cache){
        this.entitiesCache = cache;
    }

    @Override
    public void register(String key, StorageGateway gateway) {
        storageGatewayCache.put(key, gateway);
    }

    @Override
    public void release(String key) {
        storageGatewayCache.remove(key);
    }



    @Override
    public void upload(StorageEntityDto entity, ByteArrayInputStream bais) {

    }

    @Override
    public void uploadAll(Collection<StorageEntityDto> entities, Collection<ByteArrayInputStream> bais) {

    }

    @Override
    public void delete(StorageEntityDto entity) {

    }

    @Override
    public void deleteAll(Collection<StorageEntityDto> entities) {

    }

    @Override
    public void find(StorageEntityDto dto) {

    }

    @Override
    public ByteArrayInputStream download(StorageEntityDto dto) {
        return null;
    }

    @Override
    public Collection<ByteArrayInputStream> downloadAll(Collection<StorageEntityDto> dto) {
        return List.of();
    }

    @Override
    public void update() {

    }

    @Override
    public void clear() {

    }
}
