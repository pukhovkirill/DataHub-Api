package com.pukhovkirill.datahub.infrastructure.file.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;

import com.pukhovkirill.datahub.util.StringHelper;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;

public class CacheableStorageServiceImpl implements StorageService {

    private final BeanFactory beanFactory;

    private final StorageEntitiesCache cache;

    private final OngoingGatewayService ongoingGateways;

    public CacheableStorageServiceImpl(StorageEntitiesCache cache,
                                       OngoingGatewayService gateways,
                                       BeanFactory factory){
        this.cache = cache;
        this.ongoingGateways = gateways;
        this.beanFactory = factory;
    }

    @Override
    public void uploadTo(String location, StorageEntityDto entity, ByteArrayInputStream bais) {
        try{
            if(cache.hasInCache(entity)){
                entity.setPath(StringHelper.generateUniqueFilename(entity.getPath()));
                entity.setName(StringHelper.extractName(entity.getPath()));

                uploadTo(location, entity, bais);
                return;
            }

            var uploadUseCase = beanFactory.getBean(
                    UploadStorageEntity.class,
                    ongoingGateways.get(location)
            );

            uploadUseCase.upload(entity, bais);
            cache.saveToCache(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFrom(String location, String path) {
        try{
            var optEntity = find(location, path);

            if(optEntity.isEmpty()){
                throw new RuntimeException("Could not find entity: " + path);
            }

            var deleteUseCase = beanFactory.getBean(
                    DeleteStorageEntity.class,
                    ongoingGateways.get(location)
            );

            deleteUseCase.delete(optEntity.get());
            cache.removeFromCache(optEntity.get());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteArrayOutputStream download(String location, String path) {
        try{
            var optEntity = find(location, path);

            if(optEntity.isEmpty())
                throw new RuntimeException("Could not find entity: " + path);

            var downloadUseCase = beanFactory.getBean(
                    DownloadStorageEntity.class,
                    ongoingGateways.get(location)
            );

            return downloadUseCase.download(optEntity.get());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Optional<StorageEntityDto> find(String location, String path){
        StorageEntityDto storageEntityDto = null;
        try{
            var cacheHits = cache.getFromCache(StringHelper.extractName(path));

            for(var hit : cacheHits){
                if(hit.getLocation().equals(location) && hit.getPath().equals(path)){
                    return Optional.of(hit);
                }
            }

            var listUseCase = beanFactory.getBean(
                    ListStorageEntity.class,
                    ongoingGateways.get(location)
            );

            var results = listUseCase.list();

            for(var result : results){
                if(result.getPath().equals(path)){
                    storageEntityDto = StorageFile.builder()
                            .name(result.getName())
                            .path(result.getPath())
                            .contentType(result.getContentType())
                            .lastModified(result.getLastModified() != null
                                    ? (Timestamp) result.getLastModified().clone()
                                    : null)
                            .size(result.getSize())
                            .location(location).build();
                    break;
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(storageEntityDto);
    }
}
