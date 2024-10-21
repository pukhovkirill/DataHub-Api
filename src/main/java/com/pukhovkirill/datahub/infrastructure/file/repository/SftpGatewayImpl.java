package com.pukhovkirill.datahub.infrastructure.file.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileAlreadyExistsException;
import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileNotFoundException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

@Service
@Scope("prototype")
public class SftpGatewayImpl implements StorageGateway {

    private final ChannelSftp client;

    private final StorageEntityFactory factory;

    public SftpGatewayImpl(ChannelSftp client, StorageEntityFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public long count() {
        try {
            client.connect();
            var files = client.ls(client.pwd());
            client.exit();
            return files == null
                    ? 0
                    : files.size();
        } catch (JSchException | SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(StorageEntity entity) {
        try{
            client.connect();

            if(!existsByPath(entity.getPath()))
                throw new SFTPFileNotFoundException(entity.getPath());

            client.rm(entity.getPath());
            client.exit();
        }catch(JSchException | SftpException e){
            throw new RuntimeException("Error deleting file", e);
        }
    }

    @Override
    public Iterable<StorageEntity> findAll() {
        List<StorageEntity> entities = new ArrayList<>();
        try{
            client.connect();
            var files = client.ls(client.pwd());
            client.exit();
            for (ChannelSftp.LsEntry file : files) {
                var entity = findByPath(file.getLongname());
                entity.ifPresent(entities::add);
            }
        }catch(JSchException | SftpException e){
            throw new RuntimeException("Error finding file", e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            client.connect();

            if(!existsByPath(path))
                throw new SFTPFileNotFoundException(path);

            SftpATTRS fileInfo = client.lstat(path);
            client.get(path, baos);

            client.exit();

            StorageEntity storageEntity = factory.restore(
                    path,
                    new Timestamp((long) fileInfo.getATime() * 1000),
                    fileInfo.getSize(),
                    baos.toByteArray()
            );

            return Optional.of(storageEntity);
        }catch(JSchException | SftpException | IOException e){
            throw new RuntimeException("Error finding file", e);
        }
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(entity.getData())){
            client.connect();

            if(existsByPath(entity.getPath()))
                throw new SFTPFileAlreadyExistsException(entity.getPath());

            client.put(bais, entity.getPath());

            client.exit();

            return entity;
        }catch(JSchException | SftpException | IOException e){
            throw new RuntimeException("Error loading file", e);
        }
    }

    @Override
    public boolean existsByPath(String path) {
        try{
            client.connect();
            var files = client.ls(client.pwd());
            client.exit();
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
}
