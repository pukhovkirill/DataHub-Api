package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;

public class DownloadStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DownloadStorageEntityImpl downloadStorageEntityImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        downloadStorageEntityImpl = new DownloadStorageEntityImpl(storageGateway);
    }

    @Test
    public void downloadSuccess() {
        String path = "/test/path";
        byte[] data = "test data".getBytes();

        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path(path)
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        StorageEntity entity = mock(StorageEntity.class);
        when(entity.getData()).thenReturn(data);

        when(storageGateway.findByPath(path)).thenReturn(Optional.of(entity));

        ByteArrayOutputStream result = downloadStorageEntityImpl.download(dto);

        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(data, result.toByteArray());
        verify(storageGateway, times(1)).findByPath(path);
    }

    @Test
    public void downloadThrowsStorageEntityNotFoundException() {
        String path = "/invalid/path";
        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path(path)
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        when(storageGateway.findByPath(path)).thenReturn(Optional.empty());

        StorageEntityNotFoundException exception = assertThrows(
                StorageEntityNotFoundException.class,
                () -> downloadStorageEntityImpl.download(dto)
        );

        Assertions.assertEquals(String.format("Could not find storage entity with name '%s'", path), exception.getMessage());
        verify(storageGateway, times(1)).findByPath(path);
    }

    @Test
    public void downloadThrowsRuntimeExceptionOnIOException() {
        String path = "/some/path";

        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path(path)
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        StorageEntity entity = mock(StorageEntity.class);

        when(storageGateway.findByPath(path)).thenReturn(Optional.of(entity));
        when(entity.getData()).thenThrow(new RuntimeException(new IOException("Simulated IOException")));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> downloadStorageEntityImpl.download(dto)
        );

        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
        verify(storageGateway, times(1)).findByPath(path);
    }
}
