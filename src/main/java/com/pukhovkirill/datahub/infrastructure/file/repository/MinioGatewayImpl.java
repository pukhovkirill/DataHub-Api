package com.pukhovkirill.datahub.infrastructure.file.repository;

import java.util.Optional;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import org.springframework.stereotype.Service;

@Service
public class MinioGatewayImpl implements StorageGateway {

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(StorageEntity storageEntity) {

    }

    @Override
    public void deleteAll(StorageEntity storageEntity) {

    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public Iterable<StorageEntity> findAll() {
        return null;
    }

    @Override
    public Optional<StorageEntity> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public StorageEntity save(StorageEntity entity) {
        return null;
    }

    @Override
    public Iterable<StorageEntity> saveAll(Iterable<StorageEntity> entities) {
        return null;
    }
}
