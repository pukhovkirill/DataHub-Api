package com.pukhovkirill.datahub.infrastructure.file.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Files;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

@Service
@Scope("prototype")
public class FtpGatewayImpl implements StorageGateway {

    private final FTPClient client;

    private final StorageEntityFactory factory;

    public FtpGatewayImpl(FTPClient client, StorageEntityFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public long count() {
        try{
            return client.listFiles() == null
                    ? 0
                    : client.listFiles().length;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(StorageEntity entity) {
        try{
            if(client.deleteFile(entity.getPath()))
                throw new RuntimeException("Error deleting file");
        }catch(Exception e){
            throw new RuntimeException(e);
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
            FTPFile[] files = client.listFiles();
            for (FTPFile file : files) {
                var entity = findByPath(file.getName());

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
            File tmpFile = new File(path);
            OutputStream outputStream = new FileOutputStream(tmpFile);
            client.retrieveFile(path, outputStream);

            FTPFile fileInfo = client.mlistFile(path);

            StorageEntity storageEntity = factory.restore(
                    path,
                    new Timestamp(fileInfo.getTimestamp().getTimeInMillis()),
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

            OutputStream os = new FileOutputStream(localFile);
            os.write(entity.getData());
            os.close();

            boolean success = client.storeFile(entity.getName(), new FileInputStream(localFile));

            if(!success) throw new RuntimeException("Error loading file");

            return entity;
        }catch(Exception e){
            throw new RuntimeException(e);
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
            FTPFile[] files = client.listFiles();
            for (FTPFile file : files) {
                if (file.getName().equals(path)) {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
}
