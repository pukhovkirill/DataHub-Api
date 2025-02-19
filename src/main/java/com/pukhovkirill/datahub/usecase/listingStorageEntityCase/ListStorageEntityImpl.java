package com.pukhovkirill.datahub.usecase.listingStorageEntityCase;

import java.util.ArrayList;
import java.util.Collection;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

public class ListStorageEntityImpl implements ListStorageEntity {

    private final StorageGateway gateway;

    public ListStorageEntityImpl(StorageGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Collection<StorageEntity> list() {
        Collection<StorageEntity> entities = new ArrayList<>();

        try{
            var results = gateway.findAll();

            for (var result : results)
                entities.add(result);

        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return entities;
    }
}
