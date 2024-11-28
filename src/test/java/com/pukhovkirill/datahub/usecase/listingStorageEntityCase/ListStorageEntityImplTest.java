package com.pukhovkirill.datahub.usecase.listingStorageEntityCase;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListStorageEntityImplTest {

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private ListStorageEntityImpl listStorageEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testList(){
        final int n = 10;
        Collection<StorageEntity> collection = new ArrayList<>();
        for(int i = 0; i < n; i++){
            collection.add(mock(StorageEntity.class));
        }
        when(storageGateway.findAll()).thenReturn(collection);


        var result = listStorageEntity.list();


        Assertions.assertNotNull(result);
        Assertions.assertEquals(n, result.size());
        for(var entity : result){
            Assertions.assertTrue(collection.contains(entity));
        }
    }

    @Test
    public void testListWithException(){
        when(storageGateway.findAll()).thenReturn(null);


        RuntimeException exception = Assert.assertThrows(
                RuntimeException.class,
                () -> listStorageEntity.list()
        );


        Assertions.assertNotNull(exception);
        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

}
