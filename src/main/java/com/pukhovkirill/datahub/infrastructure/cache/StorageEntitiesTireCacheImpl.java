package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.infrastructure.collection.StorageEntityArrayTire;
import com.pukhovkirill.datahub.infrastructure.collection.Tire;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntitiesTireCacheImpl implements StorageEntitiesCache {

    public static final Tire<StorageEntityDto> cache;

    static{
        cache = new StorageEntityArrayTire();
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
