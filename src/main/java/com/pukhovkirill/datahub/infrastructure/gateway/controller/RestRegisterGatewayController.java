package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import org.springframework.http.ResponseEntity;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

public abstract class RestRegisterGatewayController {

    private final OngoingGatewayService ongoingGatewayService;

    protected RestRegisterGatewayController(OngoingGatewayService ongoingGatewayService) {
        this.ongoingGatewayService = ongoingGatewayService;
    }

    protected ResponseEntity<String> register(String key,
                                           String protocol, String server, int port,
                                           String username, String password,
                                           String workingDirectory) {
        StorageGateway gateway = getStorageGateway(key,
                                                   protocol, server, port,
                                                   username, password,
                                                   workingDirectory);
        ongoingGatewayService.register(key, gateway);
        return ResponseEntity.ok("success");
    }

    protected abstract StorageGateway getStorageGateway(String key,
                                                        String protocol, String server, int port,
                                                        String username, String password,
                                                        String workingDirectory);
}
