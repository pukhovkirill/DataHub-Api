package com.pukhovkirill.datahub.common.initial;

import java.util.List;

import com.pukhovkirill.datahub.util.CredentialsSaver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.CommandLineRunner;

import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.factory.StorageGatewayFactory;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class GatewayReconnectRunner implements CommandLineRunner {

    private final BeanFactory beanFactory;

    private final OngoingGatewayService ongoingGateways;

    public GatewayReconnectRunner(OngoingGatewayService ongoingGateways, BeanFactory beanFactory) {
        this.ongoingGateways = ongoingGateways;
        this.beanFactory = beanFactory;
    }

    @Order(5)
    @Override
    public void run(String... args) {
        List<GatewayCredentials> gateways = CredentialsSaver.loadCredentials();

        for (GatewayCredentials credentials : gateways) {
            var factory = beanFactory.getBean(
                    credentials.getProtocol(),
                    StorageGatewayFactory.class
            );
            var gateway = factory.create(credentials);
            ongoingGateways.register(credentials.getKey(), gateway);
        }
    }
}
