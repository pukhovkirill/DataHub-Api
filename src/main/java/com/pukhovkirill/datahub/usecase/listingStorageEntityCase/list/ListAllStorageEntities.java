package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.list;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface ListAllStorageEntities {
    Iterable<StorageEntityDto> getAllStorageEntities();
}
