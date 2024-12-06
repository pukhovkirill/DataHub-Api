package com.pukhovkirill.datahub.infrastructure.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntitiesLRUCacheImpl implements StorageEntitiesCache {

    private static final ConcurrentMap<String, List<StorageEntityDto>> entityByName;

    private static final List<StorageEntityDto> cache;

    private final int capacity;

    private final Lock writeLock = new ReentrantLock();

    static{
        entityByName = new ConcurrentHashMap<>();
        cache = Collections.synchronizedList(new LinkedList<>());
    }

    public StorageEntitiesLRUCacheImpl(int capacity) {
        this.capacity = capacity;
    }

    private void acquireWriteLock() {
        writeLock.lock();
    }

    private void releaseWriteLock() {
        writeLock.unlock();
    }

    @Override
    public Collection<StorageEntityDto> getFromCache(String key) {
        if(key == null) return new ArrayList<>();
        Collection<StorageEntityDto> result = new ArrayList<>();

        acquireWriteLock();
        try {
            List<StorageEntityDto> entities;

            if(!(entities = entityByName.getOrDefault(key, new ArrayList<>())).isEmpty()){
                for(StorageEntityDto entity : entities){
                    cache.remove(entity);
                    cache.addFirst(entity);
                    result.add(entity.clone());
                }
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

            if(cache.size() == capacity){
                removeFromCache(cache.getLast());
            }

            var list = entityByName.getOrDefault(entity.getName(), new ArrayList<>());
            if(!list.contains(entity)){
                list.add(entity);
                entityByName.put(entity.getName(), list);
                cache.addFirst(entity);
            }
        }finally {
            releaseWriteLock();
        }
    }

    @Override
    public void removeFromCache(StorageEntityDto key) {
        acquireWriteLock();
        try{
            StorageEntityDto entity = key.clone();

            var list = entityByName.get(entity.getName());
            if(list.contains(entity)){
                list.remove(entity);
                if(list.isEmpty()){
                    entityByName.remove(entity.getName());
                }else{
                    entityByName.put(entity.getName(), list);
                }
            }

            cache.remove(entity);
        }finally {
            releaseWriteLock();
        }
    }

    @Override
    public boolean hasInCache(StorageEntityDto key) {
        acquireWriteLock();
        try{
            var list = entityByName.get(key.getName());
            if(list != null && list.contains(key))
                return true;
        }finally {
            releaseWriteLock();
        }
        return false;
    }

    @Override
    public void clearCache() {
        acquireWriteLock();
        try{
            entityByName.clear();
            cache.clear();
        }finally {
            releaseWriteLock();
        }
    }
}
