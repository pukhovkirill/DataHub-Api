package com.pukhovkirill.datahub.infrastructure.file.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pukhovkirill.datahub.entity.exception.StorageEntityAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

import static com.pukhovkirill.datahub.util.TimeConverter.toTimestamp;

@Service
@Scope("prototype")
public class MinioGatewayImpl implements StorageGateway {

    @Value("${minio.default-bucket-name}")
    private String BUCKET_NAME;

    private final MinioClient client;

    private final StorageEntityFactory factory;

    public MinioGatewayImpl(MinioClient client, StorageEntityFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public long count() {
        Iterable<Result<Item>> results = client.listObjects(
                ListObjectsArgs.builder().bucket(BUCKET_NAME).build());

        int count = 0;
        for(Result<Item> ignored : results)
            count += 1;

        return count;
    }

    @Override
    public void delete(StorageEntity entity) {
        try{
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(entity.getPath())
                            .build());
        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<StorageEntity> findAll() {
        List<StorageEntity> entities = new ArrayList<>();
        try{
            Iterable<Result<Item>> results = client.listObjects(
                    ListObjectsArgs.builder().bucket(BUCKET_NAME).build());

            for(Result<Item> result : results) {
                var item = result.get();

                var entity = findByPath(item.objectName());

                entity.ifPresent(entities::add);
            }

        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        try{
            var result = Optional.ofNullable(client.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(path).build()));

            if(result.isEmpty())
                throw new StorageEntityNotFoundException(path);

            var item = result.get();

            var baos = new ByteArrayOutputStream();

            InputStream is = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .build());

            byte[] buff = new byte[1024];

            int count;
            while ((count = is.read(buff)) >= 0){
                baos.write(buff, 0, count);
            }

            var entity = factory.restore(
                    path,
                    toTimestamp(item.lastModified()),
                    item.size(),
                    baos.toByteArray()
            );

            baos.close();
            is.close();

            return Optional.of(entity);
        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        try{
            if(existsByPath(entity.getPath()))
                throw new StorageEntityAlreadyExistsException(entity.getPath());

            byte[] buff = entity.getData();

            var putBuilder = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(entity.getPath())
                    .stream(new ByteArrayInputStream(buff), entity.getSize(), -1);

            client.putObject(putBuilder.build());
            return entity;
        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByPath(String path) {
        try{
            var result = Optional.ofNullable(client.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(path).build()));

            return result.isPresent();
        }catch(Exception e){
            return false;
        }
    }
}
