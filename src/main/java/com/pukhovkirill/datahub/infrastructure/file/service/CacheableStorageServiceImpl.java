package com.pukhovkirill.datahub.infrastructure.file.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import com.pukhovkirill.datahub.util.StringHelper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;

@Service
@Scope("prototype")
public class CacheableStorageServiceImpl implements StorageService {

    private final BeanFactory beanFactory;

    private final StorageEntitiesCache cache;

    private final OngoingGatewayService ongoingGateways;

    public CacheableStorageServiceImpl(@Qualifier("tireCacheService")
                                       StorageEntitiesCache cache,
                                       OngoingGatewayService gateways,
                                       BeanFactory factory){
        this.cache = cache;
        this.ongoingGateways = gateways;
        this.beanFactory = factory;
    }

    @Override
    public void uploadTo(String location, StorageEntityDto entity, ByteArrayInputStream bais) {
        try{
            var uploadUseCase = beanFactory.getBean(
                    UploadStorageEntity.class,
                    ongoingGateways.get(location)
            );
            uploadUseCase.upload(entity, bais);

            cache.saveToCache(entity.getName(), entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFrom(String location, String path) {
        try{
            var results = cache.getFromCache(StringHelper.extractName(path));

            StorageEntityDto entity = null;
            for(var result : results){
                if(result.getLocation().equals(location) && result.getPath().equals(path)){
                    entity = result;
                    break;
                }
            }

            if(entity == null){
                throw new RuntimeException("Could not find entity: " + path);
            }

            var deleteUseCase = beanFactory.getBean(
                    DeleteStorageEntity.class,
                    ongoingGateways.get(location)
            );
            deleteUseCase.delete(entity);

            cache.removeFromCache(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteArrayOutputStream download(String location, String path) {
        try{
            var downloadUseCase = beanFactory.getBean(
                    DownloadStorageEntity.class,
                    ongoingGateways.get(location)
            );

            Collection<StorageEntityDto> results = cache.getFromCache(StringHelper.extractName(path));

            StorageEntityDto entity = null;
            for(var result : results){
                if(result.getLocation().equals(location) && result.getPath().equals(path)){
                    entity = result;
                    break;
                }
            }

            if(entity == null)
                throw new RuntimeException("Could not find entity: " + path);

            return downloadUseCase.download(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
