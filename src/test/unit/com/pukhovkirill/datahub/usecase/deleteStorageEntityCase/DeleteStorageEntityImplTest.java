package com.pukhovkirill.datahub.usecase.deleteStorageEntityCase;

import java.sql.Timestamp;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

public class DeleteStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DeleteStorageEntityImpl deleteStorageEntityImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deleteSuccess() {
        String path = "/test/path/testFile.txt";

        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        StorageEntity entity = mock(StorageEntity.class);
        when(storageGateway.findByPath(anyString())).thenReturn(Optional.of(entity));


        boolean result = deleteStorageEntityImpl.delete(storageEntity);


        assertTrue(result);
    }

    @Test
    public void deleteWithNullPointerException(){
        StorageEntityDto storageEntity = null;

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteStorageEntityImpl.delete(storageEntity)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(NullPointerException.class, exception.getCause());
    }

    @Test
    public void deleteWithStorageEntityNotFoundException() {
        String path = "/invalid/path/testFile.txt";

        StorageEntityDto storageEntity = mock(StorageEntityDto.class);
        when(storageEntity.getName()).thenReturn("testFile.txt");
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getContentType()).thenReturn("text/plain");
        when(storageEntity.getLocation()).thenReturn("internal");
        when(storageEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));

        when(storageGateway.findByPath(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteStorageEntityImpl.delete(storageEntity)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertInstanceOf(StorageEntityNotFoundException.class, exception);
        Assertions.assertEquals(String.format("Could not find storage entity with name '%s'", path), exception.getMessage());
    }
}
