package com.pukhovkirill.datahub.infrastructure.config.usecase;

import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntityImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntityImpl;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntityImpl;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntityImpl;

@Configuration
public class CommonUseCaseConfig {

    @Bean
    @Scope("prototype")
    public UploadStorageEntity externalUploadFileUseCase(StorageGateway gateway){
        return new UploadStorageEntityImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public DownloadStorageEntity externalDownloadFileUseCase(StorageGateway gateway){
        return new DownloadStorageEntityImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public DeleteStorageEntity externalDeleteFileUseCase(StorageGateway gateway){
        return new DeleteStorageEntityImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public ListStorageEntity externalListFileUseCase(StorageGateway gateway){
        return new ListStorageEntityImpl(gateway);
    }
}
