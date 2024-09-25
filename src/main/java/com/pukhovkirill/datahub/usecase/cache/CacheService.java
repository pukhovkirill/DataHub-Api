package com.pukhovkirill.datahub.usecase.cache;

public interface CacheService<T>{
    Iterable<T> getFromCache(String key);
    Iterable<T> getAllFromCache();
    void saveToCache(String key, T value);
    void removeFromCache(String key);
    boolean hasInCache(String key);
    void clearCache();
}
