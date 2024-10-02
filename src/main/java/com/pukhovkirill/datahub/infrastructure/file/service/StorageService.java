package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public interface StorageService {

    void upload(StorageEntityDto entity, ByteArrayInputStream bais);

    void uploadAll(Collection<StorageEntityDto> entities, Collection<ByteArrayInputStream> bais);

    void delete(StorageEntityDto entity);

    void deleteAll(Collection<StorageEntityDto> entities);

    void find(StorageEntityDto dto);

    ByteArrayInputStream download(StorageEntityDto dto);

    Collection<ByteArrayInputStream> downloadAll(Collection<StorageEntityDto> dto);

    void update();

    void clear();
}
