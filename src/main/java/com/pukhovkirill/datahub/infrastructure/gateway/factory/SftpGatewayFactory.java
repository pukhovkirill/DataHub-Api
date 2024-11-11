package com.pukhovkirill.datahub.infrastructure.gateway.factory;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.SftpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.SftpGatewayImpl;
import org.springframework.stereotype.Service;

@Service
public class SftpGatewayFactory implements StorageGatewayFactory{
    @Override
    public StorageGateway create(GatewayCredentials credentials) {
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
