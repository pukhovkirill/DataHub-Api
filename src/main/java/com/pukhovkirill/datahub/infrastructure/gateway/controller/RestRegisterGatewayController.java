package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.factory.StorageGatewayFactory;
import com.pukhovkirill.datahub.util.CredentialsSaver;

public abstract class RestRegisterGatewayController {

    private final OngoingGatewayService ongoingGatewayService;
    private final StorageGatewayFactory factory;

    protected RestRegisterGatewayController(OngoingGatewayService ongoingGatewayService, StorageGatewayFactory factory) {
        this.ongoingGatewayService = ongoingGatewayService;
        this.factory = factory;
    }

    protected ResponseEntity<Map<String, Object>> register(GatewayCredentials credentials) {
        StorageGateway gateway = factory.create(credentials);
        ongoingGatewayService.register(credentials.getKey(), gateway);
        CredentialsSaver.getInstance().saveCredentials(credentials);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value()));
    }
}
