package com.pukhovkirill.datahub.usecase.cache;

import java.util.Collection;

public interface CacheService<T>{
    Collection<T> getFromCache(String key);
    Collection<T> getAllFromCache();
    void saveToCache(String key, T value);
    void removeFromCache(String key);
    boolean hasInCache(String key);
    void clearCache();
}
