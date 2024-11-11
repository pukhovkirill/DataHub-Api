package com.pukhovkirill.datahub.infrastructure.file.service;

import java.util.Collection;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface SearchService {
    Collection<StorageEntityDto> list(String location);
}
