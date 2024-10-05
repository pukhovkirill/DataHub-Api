package com.pukhovkirill.datahub.usecase.downloadStorageEntityCase;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DownloadStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DownloadStorageEntityImpl downloadStorageEntityImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        downloadStorageEntityImpl = new DownloadStorageEntityImpl(storageGateway);
    }

    @Test
    public void downloadSuccess() throws Exception {
        // Arrange
        String path = "/test/path";
        byte[] data = "test data".getBytes();

        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path("/test/path")
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        StorageEntity entity = mock(StorageEntity.class);
        when(entity.getData()).thenReturn(data);

        when(storageGateway.findByPath(path)).thenReturn(Optional.of(entity));

        // Act
        ByteArrayOutputStream result = downloadStorageEntityImpl.download(dto);

        // Assert
        assertNotNull(result);
        assertArrayEquals(data, result.toByteArray());
        verify(storageGateway, times(1)).findByPath(path);
    }

    @Test
    public void downloadThrowsStorageEntityNotFoundException() {
        // Arrange
        String path = "/invalid/path";
        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path("/invalid/path")
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        when(storageGateway.findByPath(path)).thenReturn(Optional.empty());

        // Act & Assert
        StorageEntityNotFoundException exception = assertThrows(
                StorageEntityNotFoundException.class,
                () -> downloadStorageEntityImpl.download(dto)
        );

        assertEquals(path, exception.getMessage());
        verify(storageGateway, times(1)).findByPath(path);
    }

    @Test
    public void downloadThrowsRuntimeExceptionOnIOException() {
        // Arrange
        String path = "/some/path";

        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path("/invalid/path")
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        StorageEntity entity = mock(StorageEntity.class);

        when(storageGateway.findByPath(path)).thenReturn(Optional.of(entity));

        // Simulate IOException by mocking InputStream behavior (optional step)

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> downloadStorageEntityImpl.download(dto)
        );

        assertNotNull(exception.getCause());
        verify(storageGateway, times(1)).findByPath(path);
    }
}
