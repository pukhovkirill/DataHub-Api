package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.util.Map;

import com.pukhovkirill.datahub.infrastructure.gateway.factory.StorageGatewayFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;

@RestController
public class RestRegisterSftpGatewayController extends RestRegisterGatewayController{

    public RestRegisterSftpGatewayController(OngoingGatewayService ongoingGatewayService,
                                             @Qualifier("sftpGatewayFactory") StorageGatewayFactory factory) {
        super(ongoingGatewayService, factory);
    }

    @RequestMapping(value = "api/gateways/sftp", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> registerSftp(@RequestBody @Valid GatewayCredentials credentials) {
        return register(credentials);
    }
}
