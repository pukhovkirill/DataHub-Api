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

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

import static com.pukhovkirill.datahub.util.TimeConverter.toTimestamp;

// todo: minimize the number of requests to the MinIO server
@Service
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
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(Iterable<StorageEntity> entities) {
        for (StorageEntity entity : entities){
            delete(entity);
        }
    }

    @Override
    public boolean existsByPath(String path) {
        try{
            Iterable<Result<Item>> results = client.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .startAfter(path)
                            .maxKeys(1)
                            .build());

            var result = results.iterator().next();

            if(result.get().objectName().equals(path))
                return true;

        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return false;
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
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public Optional<StorageEntity> findByPath(String path) {
        try{
            Iterable<Result<Item>> results = client.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .startAfter(path)
                            .maxKeys(1)
                            .build());

            var result = results.iterator().next();

            var item = result.get();

            var out = new ByteArrayOutputStream();

            InputStream in = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .build());

            byte[] buff = new byte[1024];

            int count;
            while ((count = in.read(buff)) >= 0){
                out.write(buff, 0, count);
            }

            var entity = factory.restore(
                    path,
                    toTimestamp(item.lastModified()),
                    item.size(),
                    out.toByteArray()
            );

            out.close();
            in.close();

            return Optional.of(entity);
        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        // todo: check if exists
        try{
            byte[] buff = entity.getData();

            var putBuilder = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(entity.getPath())
                    .stream(new ByteArrayInputStream(buff), entity.getSize(), -1);

            if(!entity.getContentType().equals("folder"))
                putBuilder.contentType(entity.getContentType());

            client.putObject(
                    putBuilder.build()
            );
            return entity;
        }catch (MinioException e){
            System.err.println("Error occurred: " + e);
            System.err.println("HTTP trace: " + e.httpTrace());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Iterable<StorageEntity> saveAll(Iterable<StorageEntity> entities) {
        for(var entity : entities)
            save(entity);

        return entities;
    }
}
