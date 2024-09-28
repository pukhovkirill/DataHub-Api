package com.pukhovkirill.datahub.entity.factory;

import java.sql.Timestamp;
import java.net.URLConnection;

import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.util.StringHelper;

public class StorageEntityFactoryImpl implements StorageEntityFactory {

    @Override
    public StorageEntity create(String path, byte[] data) {
        var name = StringHelper.extractName(path);
        var contentType = URLConnection.guessContentTypeFromName(path);
        var lastModified = new Timestamp(System.currentTimeMillis());
        var size = data.length;
        return new StorageEntity(name, path, contentType, lastModified, size, data);
    }

    @Override
    public StorageEntity restore(String path, Timestamp lastModified, long size, byte[] data) {
        var name = StringHelper.extractName(path);
        var contentType = URLConnection.guessContentTypeFromName(path); //path - ? or name - ?
        return new StorageEntity(name, path, contentType, lastModified, size, data);
    }
}
