package com.pukhovkirill.datahub.infrastructure.file.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Files;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

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
            client.rm(entity.getPath());
            client.exit();
        }catch(Exception e){
            throw new RuntimeException("Error deleting file", e);
        }
    }

    @Override
    public void deleteAll(Iterable<StorageEntity> entities) {
        for(StorageEntity entity : entities){
            delete(entity);
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
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        try{
            client.connect();
            File tmpFile = new File(path);
            OutputStream outputStream = new FileOutputStream(tmpFile);
            client.get(path, outputStream);

            SftpATTRS fileInfo = client.lstat(path);

            client.exit();

            StorageEntity storageEntity = factory.restore(
                    path,
                    new Timestamp((long) fileInfo.getATime() * 1000),
                    fileInfo.getSize(),
                    Files.toByteArray(tmpFile)
            );

            return Optional.of(storageEntity);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        try{
            File localFile = new File(entity.getPath());

            client.connect();

            OutputStream os = new FileOutputStream(localFile);
            os.write(entity.getData());
            os.close();

            client.put(new FileInputStream(localFile), entity.getPath());

            client.exit();

            return entity;
        }catch(JSchException | SftpException | IOException e){
            throw new RuntimeException("Error loading file", e);
        }
    }

    @Override
    public Iterable<StorageEntity> saveAll(Iterable<StorageEntity> entities) {
        for(StorageEntity entity : entities)
            save(entity);

        return entities;
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