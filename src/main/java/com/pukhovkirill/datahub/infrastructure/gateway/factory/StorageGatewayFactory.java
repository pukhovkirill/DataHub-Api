package com.pukhovkirill.datahub.infrastructure.gateway.factory;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;

public interface StorageGatewayFactory {

    StorageGateway create(GatewayCredentials credentials);

}
