package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.Map;

import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

public abstract class RestRegisterGatewayController {

    private final OngoingGatewayService ongoingGatewayService;

    protected RestRegisterGatewayController(OngoingGatewayService ongoingGatewayService) {
        this.ongoingGatewayService = ongoingGatewayService;
    }

    protected ResponseEntity<Map<String, Object>> register(GatewayCredentials credentials) {
        StorageGateway gateway = getStorageGateway(credentials);
        ongoingGatewayService.register(credentials.getKey(), gateway);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value()));
    }

    protected abstract StorageGateway getStorageGateway(GatewayCredentials credentials);
}
