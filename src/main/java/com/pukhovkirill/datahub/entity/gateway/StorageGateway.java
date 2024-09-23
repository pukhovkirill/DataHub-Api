package com.pukhovkirill.datahub.entity.gateway;

import java.util.Optional;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

public interface StorageGateway {
    long count();

    void delete(StorageEntity storageEntity);
    void deleteAll(StorageEntity storageEntity);

    boolean existsByName(String name);

    Iterable<StorageEntity> findAll();
    Optional<StorageEntity> findByName(String name);

    StorageEntity save(StorageEntity entity);
    Iterable<StorageEntity> saveAll(Iterable<StorageEntity> entities);
}
