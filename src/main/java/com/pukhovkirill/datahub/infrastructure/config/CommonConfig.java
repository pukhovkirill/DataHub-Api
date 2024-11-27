package com.pukhovkirill.datahub.infrastructure.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pukhovkirill.datahub.infrastructure.file.service.*;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayServiceImpl;

@Configuration
public class CommonConfig {

    @Value("${application.cache.enable}")
    private boolean cacheEnabled;

    @Bean
    @Scope("singleton")
    public OngoingGatewayService ongoingGatewayService(@Qualifier("minioStorageGateway") StorageGateway gateway) {
        return new OngoingGatewayServiceImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public StorageService storageService(StorageEntitiesCache cache, OngoingGatewayService ongoingGateways, BeanFactory factory){
        return cacheEnabled
                ? new CacheableStorageServiceImpl(cache, ongoingGateways, factory)
                : new DefaultStorageServiceImpl(ongoingGateways, factory);
    }

    @Bean
    @Scope("prototype")
    public SearchService searchService(OngoingGatewayService ongoingGateways, BeanFactory factory){
        return new SearchServiceImpl(ongoingGateways, factory);
    }

    @Bean
    @Scope("prototype")
    public StorageEntityFactory storageEntityFactory() {
        return new StorageEntityFactoryImpl();
    }

}
