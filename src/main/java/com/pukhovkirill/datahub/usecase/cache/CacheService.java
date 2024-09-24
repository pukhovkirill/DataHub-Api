package com.pukhovkirill.datahub.usecase.cache;

// K - Saved object
// T - Cacheable object
public interface CacheService<K, T extends Cacheable<K>>{
    T getFromCache(String key);
    Iterable<T> getAllFromCache();
    void saveToCache(String key, T value);
    void removeFromCache(String key);
    boolean hasInCache(String key);
    void clearCache();
}
