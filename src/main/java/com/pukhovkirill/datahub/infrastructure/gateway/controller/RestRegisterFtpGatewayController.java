package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.FtpGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;

@RestController
public class RestRegisterFtpGatewayController extends RestRegisterGatewayController {

    public RestRegisterFtpGatewayController(OngoingGatewayService ongoingGatewayService) {
        super(ongoingGatewayService);
    }

    @RequestMapping(value = "api/gateways/ftp", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> registerFtp(@RequestBody @Valid GatewayCredentials credentials) {
        return register(credentials);
    }

    @Override
    protected StorageGateway getStorageGateway(GatewayCredentials credentials) {
        FtpManager manager;
        var workingDirectory = credentials.getWorkingDirectory();
        if(workingDirectory.isEmpty() || workingDirectory.isBlank())
            manager = new FtpManager(
                    credentials.getServer(), credentials.getPort(),
                    credentials.getUsername(), credentials.getPassword());
        else manager = new FtpManager(
                credentials.getServer(), credentials.getPort(),
                credentials.getUsername(), credentials.getPassword(),
                workingDirectory);

        manager.connect();
        return new FtpGatewayImpl(
                manager,
                new StorageEntityFactoryImpl()
        );
    }
}
