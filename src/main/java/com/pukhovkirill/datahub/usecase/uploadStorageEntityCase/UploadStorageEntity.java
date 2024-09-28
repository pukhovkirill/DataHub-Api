package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.io.ByteArrayInputStream;

public interface UploadStorageEntity {
    void upload(StorageEntityDto dto, ByteArrayInputStream bais);
}
