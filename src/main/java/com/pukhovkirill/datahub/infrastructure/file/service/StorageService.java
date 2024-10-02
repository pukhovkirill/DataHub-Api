package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

public interface StorageService {

    void uploadTo(String location, StorageEntityDto entity, ByteArrayInputStream bais);

    void uploadAll(String location, Collection<StorageEntityDto> entities, Collection<ByteArrayInputStream> bais);

    void delete(String location, String name);

    void deleteAll(String location, Collection<String> names);

    ByteArrayOutputStream download(StorageEntityDto entity);

    Collection<ByteArrayOutputStream> downloadAll(Collection<StorageEntityDto> entities);
}
