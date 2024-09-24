package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntityCacheObject;

import java.util.concurrent.CopyOnWriteArrayList;

public class StorageEntitiesCacheImpl implements StorageEntitiesCache {

    private static final CopyOnWriteArrayList<StorageEntityCacheObject> cache;

    static{
        cache = new CopyOnWriteArrayList<>();
    }

    @Override
    public StorageEntityCacheObject getFromCache(String key) {
        return null;
    }

    @Override
    public Iterable<StorageEntityCacheObject> getAllFromCache() {
        return null;
    }

    @Override
    public void saveToCache(String key, StorageEntityCacheObject value) {

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
