package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;

public class UploadStorageEntityImpl implements UploadStorageEntity{

    private final StorageGateway gateway;

    private final StorageEntityFactory factory;

    public UploadStorageEntityImpl(StorageGateway gateway) {
        this.gateway = gateway;
        this.factory = new StorageEntityFactoryImpl();
    }

    public void upload(StorageEntityDto dto, ByteArrayInputStream bais){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            byte[] buf = new byte[1024];

            int count;
            while((count = bais.read(buf)) >= 0)
                baos.write(buf, 0, count);

            var entity = factory.create(dto.getPath(), baos.toByteArray());
            gateway.save(entity);

        }catch(IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
