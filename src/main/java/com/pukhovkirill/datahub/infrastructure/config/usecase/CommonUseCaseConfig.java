package com.pukhovkirill.datahub.infrastructure.config.usecase;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search.StorageEntitySearch;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search.StorageEntitySearchImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CommonUseCaseConfig {
    @Bean
    @Scope("prototype")
    public StorageEntitySearch tireCacheSearchFileUseCase(@Qualifier("storageEntitiesTireCacheImpl") StorageEntitiesCache cache){
        return new StorageEntitySearchImpl(cache);
    }

    @Bean
    @Scope("prototype")
    public StorageEntitySearch defaultCacheSearchFileUseCase(@Qualifier("storageEntitiesCacheImpl") StorageEntitiesCache cache){
        return new StorageEntitySearchImpl(cache);
    }
}
