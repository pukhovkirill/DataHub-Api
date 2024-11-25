package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.util.Collection;
import java.util.List;

public class StorageEntitiesLRUCacheImpl implements StorageEntitiesCache {
    @Override
    public Collection<StorageEntityDto> getFromCache(String key) {
        return List.of();
    }

    @Override
    public Collection<StorageEntityDto> getAllFromCache() {
        return List.of();
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
