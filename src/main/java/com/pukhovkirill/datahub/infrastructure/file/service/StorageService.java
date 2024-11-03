package com.pukhovkirill.datahub.infrastructure.file.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public interface StorageService {
    void uploadTo(String location, StorageEntityDto entity, ByteArrayInputStream bais);
    void deleteFrom(String location, String path);
    ByteArrayOutputStream download(String location, String path);
}
