package com.pukhovkirill.datahub.usecase.factory;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.util.StringHelper;

import java.net.URLConnection;
import java.sql.Timestamp;

public class StorageEntityFactoryImpl implements StorageEntityFactory {

    @Override
    public StorageEntity create(String path, byte[] data) {
        var name = StringHelper.extractName(path);
        var contentType = URLConnection.guessContentTypeFromName(path);
        var lastModified = new Timestamp(System.currentTimeMillis());
        var size = data.length;
        return new StorageEntity(name, path, contentType, lastModified, size, data);
    }
}
