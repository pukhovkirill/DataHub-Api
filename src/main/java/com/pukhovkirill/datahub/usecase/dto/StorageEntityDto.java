package com.pukhovkirill.datahub.usecase.dto;

import java.sql.Timestamp;
import java.io.ByteArrayInputStream;

public interface StorageEntityDto {
    String getName();
    String getPath();
    String getContentType();
    Timestamp getLastModified();
    long getSize();
    ByteArrayInputStream getData();
}
