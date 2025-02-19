package com.pukhovkirill.datahub.entity.factory;

import java.sql.Timestamp;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

public interface StorageEntityFactory {
    StorageEntity create(String path, byte[] data);
    StorageEntity restore(String path, Timestamp lastModified, long size, byte[] data);
}
