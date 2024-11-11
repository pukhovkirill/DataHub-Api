package com.pukhovkirill.datahub.infrastructure.gateway.factory;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.FtpGatewayImpl;
import org.springframework.stereotype.Service;

@Service
public class FtpGatewayFactory implements StorageGatewayFactory{
    @Override
    public StorageGateway create(GatewayCredentials credentials) {
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
