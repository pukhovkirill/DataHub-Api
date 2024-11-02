package com.pukhovkirill.datahub.infrastructure.file.controller;

import com.pukhovkirill.datahub.infrastructure.file.repository.MinioGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.file.service.CacheableStorageServiceImpl;
import com.pukhovkirill.datahub.infrastructure.file.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void testUploadAll() {
        StorageEntityDto entity1 = mock(StorageEntityDto.class);
        StorageEntityDto entity2 = mock(StorageEntityDto.class);

        String name1 = "some1.txt";
        String name2 = "some2.txt";

        ByteArrayInputStream bais1 = new ByteArrayInputStream("data1".getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream("data2".getBytes());

        Collection<StorageEntityDto> entities = Arrays.asList(entity1, entity2);
        Collection<ByteArrayInputStream> baisCollection = Arrays.asList(bais1, bais2);

        UploadStorageEntity uploadUseCase = mock(UploadStorageEntity.class);
        when(beanFactory.getBean(eq(UploadStorageEntity.class), any())).thenReturn(uploadUseCase);
        when(ongoingGateways.get(anyString())).thenReturn(mock(MinioGatewayImpl.class));
        when(entity1.getName()).thenReturn(name1);
        when(entity2.getName()).thenReturn(name2);

        cacheableStorageService.uploadAll("location", entities, baisCollection);

        verify(uploadUseCase, times(1)).upload(entity1, bais1);
        verify(uploadUseCase, times(1)).upload(entity2, bais2);
        verify(cache, times(2)).saveToCache(anyString(), any(StorageEntityDto.class));
    }

    @Test
    public void testDelete() {
        String location = "location";
        String name = "name";
        StorageEntityDto entity = mock(StorageEntityDto.class);

        DeleteStorageEntity deleteUseCase = mock(DeleteStorageEntity.class);
        when(beanFactory.getBean(eq(DeleteStorageEntity.class), any())).thenReturn(deleteUseCase);
        when(cache.getFromCache(name)).thenReturn(Collections.singletonList(entity));
        when(entity.getLocation()).thenReturn(location);

        cacheableStorageService.delete(location, name);

        verify(deleteUseCase, times(1)).delete(entity);
        verify(cache, times(1)).removeFromCache(entity);
    }

    @Test
    public void testDeleteWhenEntityNotFound() {
        String location = "location";
        String name = "name";
        when(cache.getFromCache(name)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> cacheableStorageService.delete(location, name));
    }

    @Test
    public void testDeleteAll() {
        String location = "location";
        Collection<String> names = Arrays.asList("name1", "name2");

        StorageEntityDto entity1 = mock(StorageEntityDto.class);
        StorageEntityDto entity2 = mock(StorageEntityDto.class);

        DeleteStorageEntity deleteUseCase = mock(DeleteStorageEntity.class);
        when(beanFactory.getBean(eq(DeleteStorageEntity.class), any())).thenReturn(deleteUseCase);
        when(cache.getFromCache("name1")).thenReturn(Collections.singletonList(entity1));
        when(cache.getFromCache("name2")).thenReturn(Collections.singletonList(entity2));

        when(entity1.getLocation()).thenReturn(location);
        when(entity2.getLocation()).thenReturn(location);

        cacheableStorageService.deleteAll(location, names);

        verify(deleteUseCase, times(2)).delete(any(StorageEntityDto.class));
        verify(cache, times(2)).removeFromCache(any(StorageEntityDto.class));
    }

    @Test
    public void testDownload() {
        StorageEntityDto entity = mock(StorageEntityDto.class);

        DownloadStorageEntity downloadUseCase = mock(DownloadStorageEntity.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(beanFactory.getBean(eq(DownloadStorageEntity.class), any())).thenReturn(downloadUseCase);
        when(downloadUseCase.download(entity)).thenReturn(baos);

        ByteArrayOutputStream result = cacheableStorageService.download(entity);

        assertEquals(baos, result);
    }

    @Test
    public void testDownloadAll() {
        StorageEntityDto entity1 = mock(StorageEntityDto.class);
        StorageEntityDto entity2 = mock(StorageEntityDto.class);

        DownloadStorageEntity downloadUseCase = mock(DownloadStorageEntity.class);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        when(beanFactory.getBean(eq(DownloadStorageEntity.class), any())).thenReturn(downloadUseCase);
        when(downloadUseCase.download(entity1)).thenReturn(baos1);
        when(downloadUseCase.download(entity2)).thenReturn(baos2);

        Collection<StorageEntityDto> entities = Arrays.asList(entity1, entity2);
        Collection<ByteArrayOutputStream> results = cacheableStorageService.downloadAll(entities);

        assertEquals(new LinkedList<>(Arrays.asList(baos1, baos2)), results);
    }
}