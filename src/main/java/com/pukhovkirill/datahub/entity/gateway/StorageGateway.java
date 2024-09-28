package com.pukhovkirill.datahub.entity.gateway;

import java.util.Optional;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

public interface StorageGateway {
    long count();

    void delete(StorageEntity entity);
    void deleteAll(Iterable<StorageEntity> entities);

    boolean existsByPath(String path);

    Iterable<StorageEntity> findAll();
    Optional<StorageEntity> findByPath(String path);

    StorageEntity save(StorageEntity entity);
    Iterable<StorageEntity> saveAll(Iterable<StorageEntity> entities);
}
