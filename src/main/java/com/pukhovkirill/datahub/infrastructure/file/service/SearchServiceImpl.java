package com.pukhovkirill.datahub.infrastructure.file.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;

public class SearchServiceImpl implements SearchService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

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
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }

        return collection;
    }
}
