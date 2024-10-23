package com.pukhovkirill.datahub.infrastructure.collection;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StorageEntityArrayTireTest {
    private StorageEntityArrayTire tire;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tire = new StorageEntityArrayTire();
    }

    @Test
    void testAddEntity() {
        StorageEntityDto entityDto = mock(StorageEntityDto.class);
        StorageEntityDto cloneEntityDto = mock(StorageEntityDto.class);

        when(entityDto.getName()).thenReturn("entity1");
        when(entityDto.clone()).thenReturn(cloneEntityDto);

        tire.add(entityDto);

        Collection<StorageEntityDto> entities = tire.findAll();
        assertEquals(1, entities.size());
        assertTrue(entities.contains(cloneEntityDto));
    }

    public Collection<StorageEntityDto> setupFindAllTest(int n){
        Collection<StorageEntityDto> entities = new ArrayList<>();
        for(int i = 0; i < n; i++){
            StorageEntityDto entityDto = mock(StorageEntityDto.class);
            entities.add(entityDto);
        }
        return entities;
    }

    @Test
    void testFindAll() {
        int n = 10;
        var collection = setupFindAllTest(n);
        Set<StorageEntityDto> clones = new HashSet<>();

        int idx = 0;
        for (StorageEntityDto entityDto : collection) {
            StorageEntityDto cloneDto = mock(StorageEntityDto.class);
            clones.add(cloneDto);
            when(entityDto.getName()).thenReturn("entity"+(++idx));
            when(entityDto.clone()).thenReturn(cloneDto);
            tire.add(entityDto);
        }

        Collection<StorageEntityDto> entities = tire.findAll();

        assertEquals(n, entities.size());
        for (StorageEntityDto entityDto : entities)
            assertTrue(clones.contains(entityDto));
    }

    @Test
    void testLazyErase() {
        StorageEntityDto entityDto = mock(StorageEntityDto.class);
        StorageEntityDto cloneEntityDto = mock(StorageEntityDto.class);

        when(entityDto.getName()).thenReturn("entity3");
        when(entityDto.clone()).thenReturn(cloneEntityDto);

        when(cloneEntityDto.getName()).thenReturn("entity3");

        tire.add(entityDto);
        tire.lazyErase(cloneEntityDto);

        Collection<StorageEntityDto> entities = tire.findAll();
        assertEquals(0, entities.size());
    }

    @Test
    void testFindFuzzy() {
        StorageEntityDto entityDto = mock(StorageEntityDto.class);
        StorageEntityDto cloneEntityDto = mock(StorageEntityDto.class);

        when(entityDto.getName()).thenReturn("fuzzyName");
        when(entityDto.clone()).thenReturn(cloneEntityDto);

        when(cloneEntityDto.getName()).thenReturn("fuzzyName");

        tire.add(entityDto);
        Collection<StorageEntityDto> result = tire.findFuzzy("fuzzy");

        assertFalse(result.isEmpty());
        assertTrue(result.contains(cloneEntityDto));
    }

    @Test
    void testFill() {
        StorageEntityDto entity1 = mock(StorageEntityDto.class);
        StorageEntityDto entity2 = mock(StorageEntityDto.class);

        StorageEntityDto cloneEntityDto1 = mock(StorageEntityDto.class);
        StorageEntityDto cloneEntityDto2 = mock(StorageEntityDto.class);

        when(entity1.getName()).thenReturn("item1");
        when(entity1.clone()).thenReturn(cloneEntityDto1);

        when(entity2.getName()).thenReturn("item2");
        when(entity2.clone()).thenReturn(cloneEntityDto2);

        tire.fill(List.of(entity1, entity2));

        Collection<StorageEntityDto> entities = tire.findAll();
        assertEquals(2, entities.size());
        assertTrue(entities.contains(cloneEntityDto1));
        assertTrue(entities.contains(cloneEntityDto2));
    }

    @Test
    void testClear() {
        StorageEntityDto entityDto = mock(StorageEntityDto.class);

        when(entityDto.getName()).thenReturn("clearEntity");

        tire.add(entityDto);
        tire.clear();

        Collection<StorageEntityDto> entities = tire.findAll();
        assertEquals(0, entities.size());
    }
}
