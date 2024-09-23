package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.list;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListAllStorageEntitiesImpl implements ListAllStorageEntities{

    @Override
    public synchronized Iterable<StorageEntityDto> getAllStorageEntities() {
        return new LinkedList<>(storageEntitiesCache);
    }
}
