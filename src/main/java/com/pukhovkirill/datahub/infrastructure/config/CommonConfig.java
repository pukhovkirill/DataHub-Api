package com.pukhovkirill.datahub.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayServiceImpl;

@Configuration
public class CommonConfig {

    @Bean
    @Scope("singleton")
    public OngoingGatewayService ongoingGatewayService(@Qualifier("minioStorageGateway") StorageGateway gateway) {
        return new OngoingGatewayServiceImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public StorageEntityFactory storageEntityFactory() {
        return new StorageEntityFactoryImpl();
    }

}
