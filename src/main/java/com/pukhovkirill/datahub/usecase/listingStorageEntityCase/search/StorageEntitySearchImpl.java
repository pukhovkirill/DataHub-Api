package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.SearchCriteria;

public class StorageEntitySearchImpl implements StorageEntitySearch {

    private final Iterable<StorageEntityDto> storage;

    public StorageEntitySearchImpl(StorageEntitiesCache storage) {
        this.storage = storage.getAllFromCache();
    }

    @Override
    public Iterable<StorageEntityDto> Search(SearchCriteria criteria) {
        return null;
    }

    @Override
    public StorageEntityDto FindById() {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> FindByName(String name) {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> SearchWithFilters(SearchCriteria criteria, FilterCriteria filters) {
        return null;
    }
}
