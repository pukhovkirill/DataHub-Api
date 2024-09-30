package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.Collection;

import com.pukhovkirill.datahub.infrastructure.collection.StorageEntityArrayTire;
import com.pukhovkirill.datahub.infrastructure.collection.Tire;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import org.springframework.stereotype.Service;

@Service
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
    public void saveToCache(String key, StorageEntityDto value) {
        cache.add(value);
    }

    @Override
    public void removeFromCache(String key) {
        var results = cache.findFuzzy(key);
        for(var result : results){
            if(result.getPath().equals(key)){
                cache.lazyErase(result);
                break;
            }
        }
    }

    @Override
    public boolean hasInCache(String key) {
        var results = cache.findFuzzy(key);
        return !results.isEmpty();
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
