package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.SearchCriteria;

public class StorageEntitySearchImpl implements StorageEntitySearch {

    private final StorageEntitiesCache storageEntitiesCache;

    public StorageEntitySearchImpl(StorageEntitiesCache storage) {
        this.storageEntitiesCache = storage;
    }

    @Override
    public Iterable<StorageEntityDto> search(SearchCriteria criteria) {
        var query = criteria.getQuery();
        return storageEntitiesCache.getFromCache(query.getValue());
    }

    @Override
    public Iterable<StorageEntityDto> searchAnyWithFilters(FilterCriteria filters) {
        var collection = storageEntitiesCache.getAllFromCache();

        for(var filter : filters.getAllFilter())
            filter.filter(collection);

        return collection;
    }

    @Override
    public Iterable<StorageEntityDto> searchWithFilters(SearchCriteria criteria, FilterCriteria filters) {
        var query = criteria.getQuery();
        var collection = storageEntitiesCache.getFromCache(query.getValue());

        for(var filter : filters.getAllFilter())
            filter.filter(collection);

        return collection;
    }
}
