package com.pukhovkirill.datahub.infrastructure.config.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pukhovkirill.datahub.infrastructure.cache.StorageEntitiesLRUCacheImpl;
import com.pukhovkirill.datahub.infrastructure.cache.StorageEntitiesTireCacheImpl;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;

@Configuration
public class CacheConfig {

    @Value("${application.cache.type}")
    private String type;

    @Value("${application.cache.lru.capacity}")
    private int capacity;

    @Bean
    @Scope("singleton")
    public StorageEntitiesCache storageEntitiesCache() {
        if(type.equals("LRU"))
            return new StorageEntitiesLRUCacheImpl(capacity);

        if(type.equals("TIRE"))
            return new StorageEntitiesTireCacheImpl();

        throw new RuntimeException("Cache type not supported");
    }

}
