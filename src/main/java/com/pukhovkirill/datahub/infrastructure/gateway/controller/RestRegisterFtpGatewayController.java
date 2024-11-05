package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.FtpGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

@RestController
public class RestRegisterFtpGatewayController extends RestRegisterGatewayController {

    public RestRegisterFtpGatewayController(OngoingGatewayService ongoingGatewayService) {
        super(ongoingGatewayService);
    }

    @RequestMapping(value = "api/gateways/ftp", method = RequestMethod.POST)
    public ResponseEntity<String> registerFtp(@RequestParam("key") String key,
                                              @RequestParam("protocol") String protocol,
                                              @RequestParam("server") String server,
                                              @RequestParam("port") int port,
                                              @RequestParam("username") String username,
                                              @RequestParam("password") String password,
                                              @RequestParam("working-directory") String workingDirectory) {
        return register(key, protocol, server, port, username, password, workingDirectory);
    }

    @Override
    protected StorageGateway getStorageGateway(String key,
                                               String protocol, String server, int port,
                                               String username, String password,
                                               String workingDirectory) {
        FtpManager manager;
        if(workingDirectory.isEmpty() || workingDirectory.isBlank())
            manager = new FtpManager(server, port, username, password);
        else manager = new FtpManager(server, port, username, password, workingDirectory);

        manager.connect();
        return new FtpGatewayImpl(
                manager,
                new StorageEntityFactoryImpl()
        );
    }
}
