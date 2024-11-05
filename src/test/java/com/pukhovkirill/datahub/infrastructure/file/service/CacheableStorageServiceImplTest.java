package com.pukhovkirill.datahub.infrastructure.file.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.util.StringHelper;
import org.junit.jupiter.api.Assertions;
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

import com.pukhovkirill.datahub.infrastructure.gateway.repository.MinioGatewayImpl;
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
        String name = "/file";

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
    public void testUploadToWhenEntityAlreadyExists() {
        StorageEntityDto mockEntity = mock(StorageEntityDto.class);
        String location = "internal";
        String path = "/file";

        ByteArrayInputStream bais = new ByteArrayInputStream("data".getBytes());

        UploadStorageEntity uploadUseCase = mock(UploadStorageEntity.class);
        when(cache.getFromCache(anyString())).thenReturn(Collections.singletonList(mockEntity));
        when(beanFactory.getBean(eq(UploadStorageEntity.class), any())).thenReturn(uploadUseCase);
        when(ongoingGateways.get(anyString())).thenReturn(mock(MinioGatewayImpl.class));
        when(mockEntity.getPath()).thenReturn(path);
        when(mockEntity.getName()).thenReturn(StringHelper.extractName(path));
        when(mockEntity.getLocation()).thenReturn(location);

        StorageEntityDto entity = StorageFile.builder()
                .name("file")
                .path("/file")
                .location("internal")
                .build();

        cacheableStorageService.uploadTo(location, entity, bais);

        verify(uploadUseCase, times(1)).upload(entity, bais);
        verify(cache, times(1)).saveToCache(mockEntity.getName()+"(1)", entity);
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
        String path = "/path/to/name";
        when(cache.getFromCache(anyString())).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cacheableStorageService.deleteFrom(location, path)
        );

        Assertions.assertEquals("Could not find entity: " + path, exception.getCause().getMessage());
        Assertions.assertInstanceOf(RuntimeException.class, exception.getCause());
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

    @Test
    public void testDownloadWhenEntityNotFound() {
        String location = "internal";
        String path = "/path/to/name";
        when(cache.getFromCache(anyString())).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cacheableStorageService.download(location, path)
        );

        Assertions.assertEquals("Could not find entity: " + path, exception.getCause().getMessage());
        Assertions.assertInstanceOf(RuntimeException.class, exception.getCause());
    }
}