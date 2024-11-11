package com.pukhovkirill.datahub.infrastructure.file.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;

@Service
@Scope("prototype")
public class SearchServiceImpl implements SearchService {

    private final BeanFactory beanFactory;

    private final OngoingGatewayService ongoingGateways;

    public SearchServiceImpl(OngoingGatewayService gateways, BeanFactory factory){
        this.ongoingGateways = gateways;
        this.beanFactory = factory;
    }

    @Override
    public Collection<StorageEntityDto> list(String location) {
        Collection<StorageEntityDto> collection = new ArrayList<>();

        try{
            var listUseCase = beanFactory.getBean(
                    ListStorageEntity.class,
                    ongoingGateways.get(location)
            );

            var results = listUseCase.list();
            for(var result : results){
                var storageEntityDto = StorageFile.builder()
                        .name(result.getName())
                        .path(result.getPath())
                        .contentType(result.getContentType())
                        .lastModified(result.getLastModified() != null
                                ? (Timestamp) result.getLastModified().clone()
                                : null)
                        .size(result.getSize())
                        .location(location).build();
                collection.add(storageEntityDto);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return collection;
    }
}
