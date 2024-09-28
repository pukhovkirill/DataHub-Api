package com.pukhovkirill.datahub.usecase.deleteStorageEntityCase;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface DeleteStorageEntity {
    boolean delete(StorageEntityDto dto);
}
