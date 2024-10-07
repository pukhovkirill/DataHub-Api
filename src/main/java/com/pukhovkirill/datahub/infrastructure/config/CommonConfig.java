package com.pukhovkirill.datahub.infrastructure.config;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.file.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.file.service.OngoingGatewayServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CommonConfig {

    @Bean
    @Scope("singleton")
    public OngoingGatewayService ongoingGatewayService(@Qualifier("minioStorageGateway") StorageGateway gateway) {
        return new OngoingGatewayServiceImpl(gateway);
    }

}
