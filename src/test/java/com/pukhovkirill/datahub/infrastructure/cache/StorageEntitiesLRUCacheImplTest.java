package com.pukhovkirill.datahub.infrastructure.cache;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

class StorageEntitiesLRUCacheImplTest {

    private final static int CACHE_CAPACITY = 2;

    private StorageEntitiesLRUCacheImpl cache;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cache = new StorageEntitiesLRUCacheImpl(CACHE_CAPACITY);
        cache.clearCache();
    }

    @Test
    public void testGetFromCache() throws NoSuchFieldException, IllegalAccessException,
                                          InvocationTargetException, NoSuchMethodException,
                                          InstantiationException {
        cache.clearCache();

        StorageEntityDto storageEntity1 = StorageFile.builder().name("file1.txt").build();
        cache.saveToCache(storageEntity1);

        StorageEntityDto storageEntity2 = StorageFile.builder().name("file2.txt").build();
        cache.saveToCache(storageEntity2);

        var result = (List<StorageEntityDto>) cache.getFromCache("file1.txt");

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertNotNull(result);
        assertEquals(storageEntity1, result.getFirst());

        assertEquals(storageEntity2, list.getLast());
        assertEquals(storageEntity1, list.getFirst());
    }

    @Test
    public void testGetFromCacheWhenEntityNotFound() {
        cache.clearCache();

        StorageEntityDto storageEntity2 = StorageFile.builder().name("file2.txt").build();
        cache.saveToCache(storageEntity2);

        var result = (List<StorageEntityDto>) cache.getFromCache("file1.txt");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFromCacheWhenKeyIsNull(){
        cache.clearCache();

        var result = (List<StorageEntityDto>) cache.getFromCache(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllFromCache() {
        cache.clearCache();

        StorageEntityDto storageEntity1 = StorageFile.builder().name("file1.txt").build();
        cache.saveToCache(storageEntity1);

        StorageEntityDto storageEntity2 = StorageFile.builder().name("file2.txt").build();
        cache.saveToCache(storageEntity2);

        StorageEntityDto storageEntity3 = StorageFile.builder().name("file3.txt").build();
        cache.saveToCache(storageEntity3);

        var results = (List<StorageEntityDto>) cache.getAllFromCache();

        assertNotNull(results);
        assertEquals(CACHE_CAPACITY, results.size());
    }

    @Test
    public void testSaveToCache() throws NoSuchFieldException, IllegalAccessException,
                                         InvocationTargetException, NoSuchMethodException,
                                         InstantiationException {
        cache.clearCache();

        StorageEntityDto storageEntity = StorageFile.builder().name("file.txt").build();

        cache.saveToCache(storageEntity);

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertEquals(1, list.size());
        assertEquals(storageEntity, list.getFirst());
    }

    @Test
    public void testSaveToCacheWhenSizeEqualsCapacity() throws NoSuchFieldException, IllegalAccessException,
                                                               InvocationTargetException, NoSuchMethodException,
                                                               InstantiationException {
        cache.clearCache();

        StorageEntityDto storageEntity1 = StorageFile.builder().name("file1.txt").build();
        cache.saveToCache(storageEntity1);

        StorageEntityDto storageEntity2 = StorageFile.builder().name("file2.txt").build();
        cache.saveToCache(storageEntity2);

        StorageEntityDto storageEntity3 = StorageFile.builder().name("file3.txt").build();
        cache.saveToCache(storageEntity3);

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertEquals(storageEntity2, list.getLast());
        assertEquals(storageEntity3, list.getFirst());
        assertFalse(list.contains(storageEntity1));
    }

    @Test
    public void testSaveToCacheWhenEntityAlreadyExists() throws NoSuchFieldException, IllegalAccessException,
                                                                InvocationTargetException, NoSuchMethodException,
                                                                InstantiationException {
        cache.clearCache();

        StorageEntityDto storageEntity1 = StorageFile.builder().name("file1.txt").build();
        cache.saveToCache(storageEntity1);

        StorageEntityDto storageEntity2 = StorageFile.builder().name("file1.txt").build();
        cache.saveToCache(storageEntity2);

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertEquals(1, list.size());
        assertTrue(list.contains(storageEntity1));
    }

    @Test
    public void testRemoveFromCache() throws NoSuchFieldException, IllegalAccessException,
                                             InvocationTargetException, NoSuchMethodException,
                                             InstantiationException {
        cache.clearCache();

        StorageEntityDto storageEntity = StorageFile.builder().name("file.txt").build();
        cache.saveToCache(storageEntity);

        cache.removeFromCache(storageEntity);

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertTrue(list.isEmpty());
    }

    @Test
    public void testHasInCache() {
        cache.clearCache();

        StorageEntityDto storageEntity = StorageFile.builder().name("file.txt").build();
        cache.saveToCache(storageEntity);

        StorageEntityDto findStorage = StorageFile.builder().name("file.txt").build();

        var actual = cache.hasInCache(findStorage);

        assertTrue(actual);
    }

    @Test
    public void testHasInCacheWhenEntityNotFound() {
        cache.clearCache();

        StorageEntityDto findStorage = StorageFile.builder().name("file.txt").build();

        var actual = cache.hasInCache(findStorage);

        assertFalse(actual);
    }

    @Test
    public void testClearCache() throws NoSuchFieldException, IllegalAccessException,
                                        InvocationTargetException, NoSuchMethodException,
                                        InstantiationException {
        cache.clearCache();

        var list = getFieldValue(
                cache.getClass(),
                LinkedList.class,
                List.class,
                "cache"
        );

        assertTrue(list.isEmpty());
    }

    private <T> T getFieldValue(Class<?> clazz,
                                Class<?> fieldClazz,
                                Class<T> resultClazz,
                                String fieldName) throws NoSuchFieldException, IllegalAccessException,
                                                         NoSuchMethodException, InvocationTargetException,
                                                         InstantiationException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return resultClazz.cast(field.get(fieldClazz.getDeclaredConstructor().newInstance()));
    }
}