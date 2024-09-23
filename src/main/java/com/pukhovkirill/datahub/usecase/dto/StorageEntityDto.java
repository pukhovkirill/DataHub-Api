package com.pukhovkirill.datahub.usecase.dto;

import java.sql.Timestamp;

import com.pukhovkirill.datahub.usecase.cache.Cacheable;

public interface StorageEntityDto extends Cacheable {
    String getName();
    String getPath();
    String getContentType();
    Timestamp getLastModified();
    long getSize();
}
