package com.pukhovkirill.datahub.infrastructure.file.service;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.LinkedList;

@Service
@Scope("prototype")
public class CacheableStorageServiceImpl implements StorageService {

    private final BeanFactory beanFactory;

    private final StorageEntitiesCache cache;

    private final OngoingGatewayService ongoingGateways;

    public CacheableStorageServiceImpl(@Qualifier("tireCacheService") StorageEntitiesCache cache, OngoingGatewayService gateways, BeanFactory factory){
        this.cache = cache;
        this.ongoingGateways = gateways;
        this.beanFactory = factory;
    }

    @Override
    public void uploadTo(String location, StorageEntityDto entity, ByteArrayInputStream bais) {
        var uploadUseCase = beanFactory.getBean(UploadStorageEntity.class, ongoingGateways.get(location));
        uploadUseCase.upload(entity, bais);

        cache.saveToCache(entity.getName(), entity);
    }

    @Override
    public void uploadAll(String location, Collection<StorageEntityDto> entities, Collection<ByteArrayInputStream> bais) {
        var entIter = entities.iterator();
        var byteIter = bais.iterator();

        while (entIter.hasNext() && byteIter.hasNext()) {
            uploadTo(location, entIter.next(), byteIter.next());
        }
    }

    @Override
    public void delete(String location, String name) {
        var results = cache.getFromCache(name);

        StorageEntityDto entity = null;
        for(var result : results){
            if(result.getLocation().equals(location)){
                entity = result;
                break;
            }
        }

        if(entity == null){
            throw new RuntimeException("Could not find entity with name: " + name);
        }

        var deleteUseCase = beanFactory.getBean(DeleteStorageEntity.class, ongoingGateways.get(location));
        deleteUseCase.delete(entity);

        cache.removeFromCache(entity);
    }

    @Override
    public void deleteAll(String location, Collection<String> names) {
        for(String name : names){
            delete(location, name);
        }
    }

    @Override
    public ByteArrayOutputStream download(StorageEntityDto entity) {
        var downloadUseCase = beanFactory.getBean(DownloadStorageEntity.class, ongoingGateways.get(entity.getLocation()));
        return downloadUseCase.download(entity);
    }

    @Override
    public Collection<ByteArrayOutputStream> downloadAll(Collection<StorageEntityDto> entities) {
        Collection<ByteArrayOutputStream> collectionBaos = new LinkedList<>();

        for(var entity : entities){
            collectionBaos.add(download(entity));
        }

        return collectionBaos;
    }
}
