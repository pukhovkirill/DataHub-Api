package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;


public class DownloadStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DownloadStorageEntityImpl downloadStorageEntityImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void downloadSuccess() {
        String path = "/test/path/testFile.txt";
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        byte[] data = "test data".getBytes();
        StorageEntity entity = mock(StorageEntity.class);
        when(entity.getData()).thenReturn(data);
        when(storageGateway.findByPath(anyString())).thenReturn(Optional.of(entity));


        ByteArrayOutputStream result = downloadStorageEntityImpl.download(storageEntity);


        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(data, result.toByteArray());
    }

    @Test
    public void downloadWithIOException() {
        String path = "/test/path/testFile.txt";
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        StorageEntity entity = mock(StorageEntity.class);
        when(storageGateway.findByPath(anyString())).thenReturn(Optional.of(entity));
        when(entity.getData()).thenThrow(new RuntimeException(new IOException()));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> downloadStorageEntityImpl.download(storageEntity)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    public void downloadWithNullPointerException() {
        StorageEntityDto storageEntity = null;


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> downloadStorageEntityImpl.download(storageEntity)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(NullPointerException.class, exception.getCause());
    }

    @Test
    public void downloadWithStorageEntityNotFoundException() {
        String path = "/invalid/path/testFile.txt";
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        when(storageGateway.findByPath(path)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> downloadStorageEntityImpl.download(storageEntity)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertInstanceOf(StorageEntityNotFoundException.class, exception);
        Assertions.assertEquals(String.format("Could not find storage entity with name '%s'", path), exception.getMessage());
    }
}
