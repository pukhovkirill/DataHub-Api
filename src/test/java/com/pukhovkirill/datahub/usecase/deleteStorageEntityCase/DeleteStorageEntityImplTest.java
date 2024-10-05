package com.pukhovkirill.datahub.usecase.deleteStorageEntityCase;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;

@SpringBootTest
public class DeleteStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DeleteStorageEntityImpl deleteStorageEntityImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteStorageEntityImpl = new DeleteStorageEntityImpl(storageGateway);
    }

    @Test
    public void deleteSuccess() {
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

        boolean result = deleteStorageEntityImpl.delete(dto);

        assertTrue(result);
        verify(storageGateway, times(1)).findByPath(path);
        verify(storageGateway, times(1)).delete(entity);
    }

    @Test
    public void deleteThrowsStorageEntityNotFoundException() {
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
                () -> deleteStorageEntityImpl.delete(dto)
        );

        assertEquals(String.format("Could not find storage entity with name '%s'", path), exception.getMessage());
        verify(storageGateway, times(1)).findByPath(path);
        verify(storageGateway, never()).delete(any(StorageEntity.class));
    }
}
