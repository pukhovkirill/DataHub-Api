package com.pukhovkirill.datahub.common.initial;

import java.util.List;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;
import com.pukhovkirill.datahub.util.CredentialsSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.CommandLineRunner;

import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.factory.StorageGatewayFactory;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(5)
@Component
public class GatewayReconnectRunner implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayReconnectRunner.class);

    private final BeanFactory beanFactory;

    private final OngoingGatewayService ongoingGateways;

    public GatewayReconnectRunner(OngoingGatewayService ongoingGateways, BeanFactory beanFactory) {
        this.ongoingGateways = ongoingGateways;
        this.beanFactory = beanFactory;
    }

    @Override
    public void run(String... args) {
        List<GatewayCredentials> gateways = CredentialsSaver.getInstance().loadCredentials();

        for (GatewayCredentials credentials : gateways) {
            var factory = beanFactory.getBean(
                    credentials.getProtocol()+"GatewayFactory",
                    StorageGatewayFactory.class
            );
            StorageGateway gateway;
            try {
                gateway = factory.create(credentials);
            }catch(FailedToServerConnectException e){
                continue;
            }
            ongoingGateways.register(credentials.getKey(), gateway);
        }
    }
}
