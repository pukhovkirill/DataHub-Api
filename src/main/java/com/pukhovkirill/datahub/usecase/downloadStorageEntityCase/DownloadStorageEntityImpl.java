package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class DownloadStorageEntityImpl implements DownloadStorageEntity {

    private final StorageGateway gateway;

    public DownloadStorageEntityImpl(StorageGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public ByteArrayOutputStream download(StorageEntityDto dto) {
        // todo: add exception cases
        try{
            var optEntity = gateway.findByPath(dto.getPath());

            if(optEntity.isEmpty())
                throw new StorageEntityNotFoundException(dto.getPath());

            var entity = optEntity.get();

            ByteArrayInputStream bais = new ByteArrayInputStream(entity.getData());

            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int count;
            while((count = bais.read(buf)) >= 0)
                baos.write(buf, 0, count);

            bais.close();

            return baos;
        }catch(IOException e){
            throw new RuntimeException(e);
        }catch(NullPointerException e){
            throw new StorageEntityNotFoundException(dto.getPath());
        }
    }
}
