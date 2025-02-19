package com.pukhovkirill.datahub.infrastructure.gateway.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileAlreadyExistsException;
import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileNotFoundException;
import com.pukhovkirill.datahub.infrastructure.external.SftpManager;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;

@Service
@Scope("prototype")
public class SftpGatewayImpl implements StorageGateway, Closeable {

    private final SftpManager manager;

    private final StorageEntityFactory factory;

    private ChannelSftp client;

    public SftpGatewayImpl(SftpManager manager, StorageEntityFactory factory) {
        this.manager = manager;
        this.factory = factory;
        this.client = manager.getClient();
    }

    @Override
    public long count() {
        connectIfNotAlive();
        try {
            var files = client.ls(client.pwd());
            return files == null
                    ? 0
                    : files.size();
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(StorageEntity entity) {
        connectIfNotAlive();
        try{
            if(!existsByPath(entity.getPath()))
                throw new SFTPFileNotFoundException(entity.getPath());

            client.rm(entity.getPath());
        }catch(SftpException e){
            throw new RuntimeException("Error deleting file", e);
        }
    }

    @Override
    public Iterable<StorageEntity> findAll() {
        connectIfNotAlive();
        List<StorageEntity> entities = new ArrayList<>();
        try{
            var files = client.ls(client.pwd());
            for (ChannelSftp.LsEntry file : files) {
                SftpATTRS fileInfo = file.getAttrs();

                var entity = factory.restore(
                        file.getLongname(),
                        new Timestamp((long) fileInfo.getATime() * 1000),
                        fileInfo.getSize(),
                        new byte[] { }
                );

                entities.add(entity);
            }
        }catch(SftpException e){
            throw new RuntimeException("Error finding file", e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        connectIfNotAlive();
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            if(!existsByPath(path))
                throw new SFTPFileNotFoundException(path);

            SftpATTRS fileInfo = client.lstat(path);
            client.get(path, baos);

            StorageEntity storageEntity = factory.restore(
                    path,
                    new Timestamp((long) fileInfo.getATime() * 1000),
                    fileInfo.getSize(),
                    baos.toByteArray()
            );

            return Optional.of(storageEntity);
        }catch(SftpException | IOException e){
            throw new RuntimeException("Error finding file", e);
        }
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        connectIfNotAlive();
        try(ByteArrayInputStream bais = new ByteArrayInputStream(entity.getData())){

            if(existsByPath(entity.getPath()))
                throw new SFTPFileAlreadyExistsException(entity.getPath());

            client.put(bais, entity.getPath());

            return entity;
        }catch(SftpException | IOException e){
            throw new RuntimeException("Error loading file", e);
        }
    }

    @Override
    public boolean existsByPath(String path) {
        connectIfNotAlive();
        try{
            var files = client.ls(client.pwd());
            for (ChannelSftp.LsEntry file : files) {
                if (file.getLongname().equals(path)) {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    private void connectIfNotAlive(){
        if(!clientIsAlive()){
            throw new FailedToServerConnectException("Server not available", "ftp");
        }
        if(!client.isConnected()){
            manager.connect();
            client = manager.getClient();
        }
    }

    private boolean clientIsAlive(){
        if(client == null) return false;
        return !client.isClosed();
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
