package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class UploadStorageEntityImpl implements UploadStorageEntity{

    private final StorageGateway gateway;

    private final StorageEntityFactory factory;

    public UploadStorageEntityImpl(StorageGateway gateway, StorageEntityFactory factory) {
        this.gateway = gateway;
        this.factory = factory;
    }

    public StorageEntity upload(StorageEntityDto dto, ByteArrayInputStream bais){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            byte[] buf = new byte[1024];

            while(bais.read(buf, 0, buf.length) > 0)
                baos.write(buf, 0, buf.length);

            var entity = factory.create(dto.getPath(), baos.toByteArray());
            return gateway.save(entity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
