package com.pukhovkirill.datahub.usecase.dto;

import java.sql.Timestamp;

public interface StorageEntityDto {
    String getName();
    void setName(String name);

    String getPath();
    void setPath(String path);

    String getContentType();

    Timestamp getLastModified();

    long getSize();

    String getLocation();

    boolean equals(Object o);

    int hashCode();

    StorageEntityDto clone();
}
