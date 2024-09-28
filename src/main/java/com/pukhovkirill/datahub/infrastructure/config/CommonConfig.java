package com.pukhovkirill.datahub.infrastructure.config;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CommonConfig {

    @Bean
    @Scope("singleton")
    public StorageEntityFactory storageEntityFactory() {
        return new StorageEntityFactoryImpl();
    }
}
