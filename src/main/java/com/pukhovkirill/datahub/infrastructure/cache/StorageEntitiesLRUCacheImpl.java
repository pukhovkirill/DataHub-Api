package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntitiesLRUCacheImpl implements StorageEntitiesCache {

    private static final ConcurrentMap<String, StorageEntityDto> entityByName;

    private static final List<StorageEntityDto> cache;

    private static final int capacity;

    private final Lock writeLock = new ReentrantLock();

    static{
        capacity = 256;
        entityByName = new ConcurrentHashMap<>();
        cache = Collections.synchronizedList(new LinkedList<>());
    }

    private void acquireWriteLock() {
        writeLock.lock();
    }

    private void releaseWriteLock() {
        writeLock.unlock();
    }

    @Override
    public Collection<StorageEntityDto> getFromCache(String key) {
        Collection<StorageEntityDto> result = new ArrayList<>();

        acquireWriteLock();
        try {
            StorageEntityDto entity;
            if((entity = entityByName.get(key)) != null){
                cache.remove(entity);
                cache.addFirst(entity);
                result.add(entity.clone());
            }
        }finally {
            releaseWriteLock();
        }

        return result;
    }

    @Override
    public Collection<StorageEntityDto> getAllFromCache() {
        Collection<StorageEntityDto> result = new ArrayList<>();

        acquireWriteLock();
        try {
            for(var entity : cache){
                result.add(entity.clone());
            }
        }finally {
            releaseWriteLock();
        }

        return result;
    }

    @Override
    public void saveToCache(StorageEntityDto value) {
        acquireWriteLock();
        try{
            StorageEntityDto entity = value.clone();

            if(entityByName.containsKey(entity.getName()))
                return;

            if(cache.size() >= capacity){
                cache.removeLast();
            }

            cache.addFirst(entity);
            entityByName.put(entity.getName(), entity);
        }finally {
            releaseWriteLock();
        }
    }

    @Override
    public void removeFromCache(StorageEntityDto key) {
        acquireWriteLock();
        try{
            if(entityByName.containsKey(key.getName())){
                var entity = entityByName.get(key.getName());
                cache.remove(entity);
                entityByName.remove(key.getName());
            }
        }finally {
            releaseWriteLock();
        }
    }

    @Override
    public boolean hasInCache(StorageEntityDto key) {
        acquireWriteLock();
        try{
            if(entityByName.containsValue(key))
                return true;
        }finally {
            releaseWriteLock();
        }
        return false;
    }

    @Override
    public void clearCache() {
        entityByName.clear();
        cache.clear();
    }
}
