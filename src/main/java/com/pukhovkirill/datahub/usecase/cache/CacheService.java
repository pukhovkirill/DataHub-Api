package com.pukhovkirill.datahub.usecase.cache;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.util.Collection;

public interface CacheService<T>{
    Collection<T> getFromCache(String key);
    Collection<T> getAllFromCache();
    void saveToCache(String key, T value);
    void removeFromCache(StorageEntityDto key);
    boolean hasInCache(StorageEntityDto key);
    void clearCache();
}
