package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.infrastructure.file.repository.MinioGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.file.service.CacheableStorageServiceImpl;
import com.pukhovkirill.datahub.infrastructure.file.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;

public class CacheableStorageServiceImplTest {

    @Mock
    private StorageEntitiesCache cache;

    @Mock
    private OngoingGatewayService ongoingGateways;

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private CacheableStorageServiceImpl cacheableStorageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cache = mock(StorageEntitiesCache.class);
        ongoingGateways = mock(OngoingGatewayService.class);
        beanFactory = mock(BeanFactory.class);
        cacheableStorageService = new CacheableStorageServiceImpl(cache, ongoingGateways, beanFactory);
    }

    @Test
    public void testUploadTo() {
        StorageEntityDto entity = mock(StorageEntityDto.class);
        String name = "some.txt";

        ByteArrayInputStream bais = new ByteArrayInputStream("data".getBytes());

        UploadStorageEntity uploadUseCase = mock(UploadStorageEntity.class);
        when(beanFactory.getBean(eq(UploadStorageEntity.class), any())).thenReturn(uploadUseCase);
        when(ongoingGateways.get(anyString())).thenReturn(mock(MinioGatewayImpl.class));
        when(entity.getName()).thenReturn(name);

        cacheableStorageService.uploadTo("internal", entity, bais);

        verify(uploadUseCase, times(1)).upload(entity, bais);
        verify(cache, times(1)).saveToCache(name, entity);
    }

    @Test
    public void testDelete() {
        String location = "internal";
        String path = "/path/to/file";
        StorageEntityDto entity = mock(StorageEntityDto.class);

        DeleteStorageEntity deleteUseCase = mock(DeleteStorageEntity.class);
        when(beanFactory.getBean(eq(DeleteStorageEntity.class), any())).thenReturn(deleteUseCase);
        when(cache.getFromCache("file")).thenReturn(Collections.singletonList(entity));
        when(entity.getLocation()).thenReturn(location);
        when(entity.getPath()).thenReturn(path);

        cacheableStorageService.deleteFrom(location, path);

        verify(deleteUseCase, times(1)).delete(entity);
        verify(cache, times(1)).removeFromCache(entity);
    }

    @Test
    public void testDeleteWhenEntityNotFound() {
        String location = "internal";
        String name = "name";
        when(cache.getFromCache(name)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> cacheableStorageService.deleteFrom(location, name));
    }

    @Test
    public void testDownload() {
        String location = "internal";
        String path = "/path/to/file";
        StorageEntityDto entity = mock(StorageEntityDto.class);

        DownloadStorageEntity downloadUseCase = mock(DownloadStorageEntity.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(beanFactory.getBean(eq(DownloadStorageEntity.class), any())).thenReturn(downloadUseCase);
        when(downloadUseCase.download(entity)).thenReturn(baos);
        when(cache.getFromCache("file")).thenReturn(Collections.singletonList(entity));
        when(entity.getLocation()).thenReturn(location);
        when(entity.getPath()).thenReturn(path);

        ByteArrayOutputStream result = cacheableStorageService.download(entity.getLocation(), entity.getPath());

        assertEquals(baos, result);
    }
}