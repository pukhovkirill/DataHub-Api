package com.pukhovkirill.datahub.usecase.dto;

import java.sql.Timestamp;

public interface StorageEntityDto {
    String getName();
    void setName(String name);

    String getPath();
    void setPath(String path);

    String getContentType();
    void setContentType(String path);

    Timestamp getLastModified();
    void setLastModified(Timestamp timestamp);

    long getSize();
    void setSize(long size);

    String getLocation();
    void setLocation(String location);

    boolean equals(Object o);

    int hashCode();

    StorageEntityDto clone();
}
