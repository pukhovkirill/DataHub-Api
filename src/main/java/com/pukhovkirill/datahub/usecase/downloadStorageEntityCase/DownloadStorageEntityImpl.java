package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
            var optEntity = gateway.findByName(dto.getPath());

            if(optEntity.isEmpty())
                throw new StorageEntityNotFoundException(dto.getPath());

            var entity = optEntity.get();

            ByteArrayInputStream bais = new ByteArrayInputStream(entity.getData());

            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((bais.read(buf, 0, buf.length)) > 0)
                baos.write(buf, 0, buf.length);

            bais.close();

            return baos;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }
}
