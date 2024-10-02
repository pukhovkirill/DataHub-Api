package com.pukhovkirill.datahub.usecase.dto;

import java.sql.Timestamp;

public interface StorageEntityDto {
    String getName();
    String getPath();
    String getContentType();
    Timestamp getLastModified();
    long getSize();
    String getLocation();
    StorageEntityDto clone();
}
