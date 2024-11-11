package com.pukhovkirill.datahub.usecase.listingStorageEntityCase;

import java.util.Collection;

import com.pukhovkirill.datahub.entity.model.StorageEntity;

public interface ListStorageEntity {
    Collection<StorageEntity> list();
}
