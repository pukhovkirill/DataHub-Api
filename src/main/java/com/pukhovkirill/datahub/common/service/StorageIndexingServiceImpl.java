package com.pukhovkirill.datahub.common.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;

@Service
public class StorageIndexingServiceImpl implements StorageIndexingService {

    private final BeanFactory beanFactory;

    private final StorageEntitiesCache cache;

    private final OngoingGatewayService ongoingGateways;

    public StorageIndexingServiceImpl(@Qualifier("tireCacheService")
                                      StorageEntitiesCache cache,
                                      OngoingGatewayService service,
                                      BeanFactory beanFactory) {
        this.cache = cache;
        this.ongoingGateways = service;
        this.beanFactory = beanFactory;
    }

    @Override
    public void indexing() {
        try{
            for(String location : ongoingGateways.list()){
                var listUseCase = beanFactory.getBean(
                        ListStorageEntity.class,
                        ongoingGateways.get(location)
                );

                var results = listUseCase.list();
                for(var entity : results){
                    StorageFile storageFile = StorageFile.builder()
                            .name(entity.getName())
                            .path(entity.getPath())
                            .contentType(entity.getContentType())
                            .lastModified(entity.getLastModified() != null
                                    ? (Timestamp) entity.getLastModified().clone()
                                    : null)
                            .size(entity.getSize())
                            .location(location)
                            .build();
                    cache.saveToCache(storageFile);
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
