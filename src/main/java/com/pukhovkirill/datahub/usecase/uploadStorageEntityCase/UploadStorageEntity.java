package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.io.ByteArrayInputStream;

public interface UploadStorageEntity {
    StorageEntity upload(StorageEntityDto dto, ByteArrayInputStream bais);
}
