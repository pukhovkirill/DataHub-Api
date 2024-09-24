package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntityCacheObject;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StorageEntityCacheObjectImpl implements StorageEntityCacheObject {

    private long expireAt;

    private StorageEntityDto storageEntity;

    @Override
    public long expireAt() {
        return expireAt;
    }

    @Override
    public StorageEntityDto getObject() {
        return storageEntity;
    }
}
