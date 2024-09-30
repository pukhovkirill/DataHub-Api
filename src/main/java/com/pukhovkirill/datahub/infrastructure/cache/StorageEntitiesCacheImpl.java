package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import org.springframework.stereotype.Service;

@Service
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
