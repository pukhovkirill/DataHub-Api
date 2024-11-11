package com.pukhovkirill.datahub.usecase.cache;

import java.util.Collection;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface CacheService<T>{
    Collection<T> getFromCache(String key);
    Collection<T> getAllFromCache();
    void saveToCache(T value);
    void removeFromCache(StorageEntityDto key);
    boolean hasInCache(StorageEntityDto key);
    void clearCache();
}
