package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.io.ByteArrayOutputStream;

public interface DownloadStorageEntity {
    ByteArrayOutputStream download(StorageEntityDto dto);
}
