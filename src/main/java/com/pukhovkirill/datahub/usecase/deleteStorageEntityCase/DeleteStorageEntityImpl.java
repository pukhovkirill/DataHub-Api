package com.pukhovkirill.datahub.usecase.deleteStorageEntityCase;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class DeleteStorageEntityImpl implements DeleteStorageEntity{

    private final StorageGateway gateway;

    public DeleteStorageEntityImpl(StorageGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public boolean delete(StorageEntityDto dto) {
        try{
            var optEntity = gateway.findByPath(dto.getPath());

            if(optEntity.isEmpty())
                throw new StorageEntityNotFoundException(dto.getPath());

            var entity = optEntity.get();

            gateway.delete(entity);
            return true;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }
}
