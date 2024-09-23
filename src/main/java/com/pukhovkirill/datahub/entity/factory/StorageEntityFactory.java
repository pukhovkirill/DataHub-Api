package com.pukhovkirill.datahub.entity.factory;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

public interface StorageEntityFactory {
    StorageEntity create(String path, byte[] data);
}
