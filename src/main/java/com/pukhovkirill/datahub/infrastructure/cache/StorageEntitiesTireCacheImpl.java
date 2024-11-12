package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.Collection;

import com.pukhovkirill.datahub.infrastructure.collection.StorageEntityArrayTire;
import com.pukhovkirill.datahub.infrastructure.collection.Tire;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntitiesTireCacheImpl implements StorageEntitiesCache {

    private static final Tire<StorageEntityDto> cache;

    static{
        cache = new StorageEntityArrayTire();
    }

    @Override
    public Collection<StorageEntityDto> getFromCache(String key) {
        return cache.findFuzzy(key);
    }

    @Override
    public Collection<StorageEntityDto> getAllFromCache() {
        return cache.findAll();
    }

    @Override
    public void saveToCache(StorageEntityDto value) {
        cache.add(value);
    }

    @Override
    public void removeFromCache(StorageEntityDto key) {
        var results = cache.findFuzzy(key.getName());
        for(var result : results){
            if(result.getLocation().equals(key.getLocation())){
                cache.lazyErase(result);
                break;
            }
        }
    }

    @Override
    public boolean hasInCache(StorageEntityDto key) {
        var results = cache.findFuzzy(key.getName());
        return !results.isEmpty() && results.contains(key);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
