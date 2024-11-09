package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.util.Map;

import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.SftpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.SftpGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

@RestController
public class RestRegisterSftpGatewayController extends RestRegisterGatewayController{

    public RestRegisterSftpGatewayController(OngoingGatewayService ongoingGatewayService) {
        super(ongoingGatewayService);
    }

    @RequestMapping(value = "api/gateways/sftp", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> registerSftp(@RequestBody GatewayCredentials credentials) {
        return register(credentials);
    }

    @Override
    protected StorageGateway getStorageGateway(GatewayCredentials credentials) {
        SftpManager manager;
        var workingDirectory = credentials.getWorkingDirectory();
        if(workingDirectory.isEmpty() || workingDirectory.isBlank())
            manager = new SftpManager(
                    credentials.getServer(), credentials.getPort(),
                    credentials.getUsername(), credentials.getPassword());
        else manager = new SftpManager(
                credentials.getServer(), credentials.getPort(),
                credentials.getUsername(), credentials.getPassword(),
                workingDirectory);

        manager.connect();
        return new SftpGatewayImpl(
                manager,
                new StorageEntityFactoryImpl()
        );
    }
}
