package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import java.io.ByteArrayInputStream;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface UploadStorageEntity {
    void upload(StorageEntityDto dto, ByteArrayInputStream bais);
}
