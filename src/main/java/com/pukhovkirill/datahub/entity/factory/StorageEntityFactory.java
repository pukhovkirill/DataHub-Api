package com.pukhovkirill.datahub.entity.factory;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

import java.sql.Timestamp;

public interface StorageEntityFactory {
    StorageEntity create(String path, byte[] data);
    StorageEntity restore(String path, Timestamp lastModified, long size, byte[] data);
}
