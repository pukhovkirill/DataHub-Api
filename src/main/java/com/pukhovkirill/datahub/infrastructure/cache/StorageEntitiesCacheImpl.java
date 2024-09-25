package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.util.concurrent.CopyOnWriteArrayList;

public class StorageEntitiesCacheImpl implements StorageEntitiesCache {

    private static final CopyOnWriteArrayList<StorageEntityCacheObject> cache;

    static{
        cache = new CopyOnWriteArrayList<>();
    }

    @Override
    public Iterable<StorageEntityDto> getFromCache(String key) {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> getAllFromCache() {
        return null;
    }

    @Override
    public void saveToCache(String key, StorageEntityDto value) {

    }

    @Override
    public void removeFromCache(String key) {

    }

    @Override
    public boolean hasInCache(String key) {
        return false;
    }

    @Override
    public void clearCache() {

    }
}
