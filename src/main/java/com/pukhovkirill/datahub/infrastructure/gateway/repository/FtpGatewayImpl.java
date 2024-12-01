package com.pukhovkirill.datahub.infrastructure.gateway.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pukhovkirill.datahub.infrastructure.exception.FTPFileAlreadyExistsException;
import com.pukhovkirill.datahub.infrastructure.exception.FTPFileNotFoundException;
import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

@Service
@Scope("prototype")
public class FtpGatewayImpl implements StorageGateway, Closeable {

    private final FtpManager manager;

    private final StorageEntityFactory factory;

    private FTPClient client;

    public FtpGatewayImpl(FtpManager manager, StorageEntityFactory factory) {
        this.manager = manager;
        this.factory = factory;
        client = manager.getClient();
    }

    @Override
    public long count() {
        connectIfNotAlive();
        try{
            return client.listFiles() == null
                    ? 0
                    : client.listFiles().length;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(StorageEntity entity) {
        connectIfNotAlive();
        try{
            if(!existsByPath(entity.getPath()))
                throw new FTPFileNotFoundException(entity.getPath());

            if(!client.deleteFile(entity.getPath()))
                throw new IOException("Error deleting file");
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<StorageEntity> findAll() {
        connectIfNotAlive();
        List<StorageEntity> entities = new ArrayList<>();
        try{
            FTPFile[] files = client.listFiles();
            for (FTPFile file : files) {

                var entity = factory.restore(
                        file.getName(),
                        new Timestamp(file.getTimestamp().getTimeInMillis()),
                        file.getSize(),
                        new byte[] { }
                );

                entities.add(entity);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        connectIfNotAlive();
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){

            if(!existsByPath(path))
                throw new FTPFileNotFoundException(path);

            FTPFile fileInfo = client.mlistFile(path);

            client.retrieveFile(path, baos);

            StorageEntity storageEntity = factory.restore(
                    path,
                    new Timestamp(fileInfo.getTimestamp().getTimeInMillis()),
                    fileInfo.getSize(),
                    baos.toByteArray()
            );

            return Optional.of(storageEntity);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        connectIfNotAlive();
        try(ByteArrayInputStream bais = new ByteArrayInputStream(entity.getData())){

            if(existsByPath(entity.getPath()))
                throw new FTPFileAlreadyExistsException(entity.getPath());

            if(!client.storeFile(entity.getPath(), bais))
                throw new IOException("Error loading file");

            return entity;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByPath(String path) {
        connectIfNotAlive();
        try{
            FTPFile[] files = client.listFiles();
            for (FTPFile file : files) {
                if (file.getName().equals(path)) {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void connectIfNotAlive(){
        if(client == null || !client.isAvailable()){
            throw new FailedToServerConnectException("Server not available", "ftp");
        }
        if(!client.isConnected()){
            manager.connect();
            client = manager.getClient();
        }
    }

    @Override
    public void close() {
        try {
            manager.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
