package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import java.io.ByteArrayOutputStream;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface DownloadStorageEntity {
    ByteArrayOutputStream download(StorageEntityDto dto);
}
