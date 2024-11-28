package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class UploadStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private UploadStorageEntityImpl uploadStorageEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpload(){
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn("/test/path/testFile.txt");
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        ByteArrayInputStream bais = new ByteArrayInputStream("test data".getBytes());


        uploadStorageEntity.upload(storageEntity, bais);


        verify(storageGateway).save(any(StorageEntity.class));
    }

    @Test
    public void testUploadWithIOException() {
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);

        ByteArrayInputStream bais = mock(ByteArrayInputStream.class);
        doThrow(new IOException()).when(bais);


        RuntimeException exception = Assert.assertThrows(
                RuntimeException.class,
                () -> uploadStorageEntity.upload(storageEntity, bais)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    public void testUploadWithNullPointerExceptionAndEmptyDto(){
        StorageEntityDto storageEntity = null;

        ByteArrayInputStream bais = new ByteArrayInputStream("test data".getBytes());


        RuntimeException exception = Assert.assertThrows(
                RuntimeException.class,
                () -> uploadStorageEntity.upload(storageEntity, bais)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(NullPointerException.class, exception.getCause());
    }

    @Test
    public void testUploadWithNullPointerExceptionAndEmptyBais(){
        StorageEntityDto storageEntity = mock(StorageEntityDto.class);

        ByteArrayInputStream bais = null;


        RuntimeException exception = Assert.assertThrows(
                RuntimeException.class,
                () -> uploadStorageEntity.upload(storageEntity, bais)
        );


        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(NullPointerException.class, exception.getCause());
    }
}
