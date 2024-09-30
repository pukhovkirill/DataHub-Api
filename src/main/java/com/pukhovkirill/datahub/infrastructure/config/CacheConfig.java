package com.pukhovkirill.datahub.infrastructure.config;

import com.pukhovkirill.datahub.infrastructure.cache.StorageEntitiesCacheImpl;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CacheConfig {

    @Bean
    @Scope("singleton")
    public StorageEntitiesCache commonCacheService(){
        return new StorageEntitiesCacheImpl();
    }

    @Bean
    @Scope("singleton")
    public StorageEntitiesCache tireCacheService() {
        return new StorageEntitiesCacheImpl();
    }

}
