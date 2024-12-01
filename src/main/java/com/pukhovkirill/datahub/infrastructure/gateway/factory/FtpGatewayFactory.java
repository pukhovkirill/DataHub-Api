package com.pukhovkirill.datahub.infrastructure.gateway.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.FtpGatewayImpl;

@Service
public class FtpGatewayFactory implements StorageGatewayFactory{

    private final static Logger LOGGER = LoggerFactory.getLogger(FtpGatewayFactory.class);

    @Override
    public StorageGateway create(GatewayCredentials credentials) {
        try{
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
        }catch(FailedToServerConnectException e){
            LOGGER.error(
                    "Failed to connect to gateway [protocol={}, server={}, port={}]",
                    credentials.getProtocol(),
                    credentials.getServer(),
                    credentials.getPort()
            );
            throw e;
        }
    }
}
