package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@Service
@Scope("prototype")
public class StorageEntitiesCacheImpl implements StorageEntitiesCache {

    private static final CopyOnWriteArrayList<StorageEntityDto> cache;

    static{
        cache = new CopyOnWriteArrayList<>();
    }

    @Override
    public Collection<StorageEntityDto> getFromCache(String key) {
        return null;
    }

    @Override
    public Collection<StorageEntityDto> getAllFromCache() {
        return null;
    }

    @Override
    public void saveToCache(StorageEntityDto value) {

    }

    @Override
    public void removeFromCache(StorageEntityDto key) {

    }

    @Override
    public boolean hasInCache(StorageEntityDto key) {
        return false;
    }

    @Override
    public void clearCache() {

    }
}
