package com.pukhovkirill.datahub.usecase.searchStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.SearchCriteria;

public class StorageEntitySearchImpl implements StorageEntitySearch {

    private final StorageEntitiesCache storageEntitiesCache;

    public StorageEntitySearchImpl(StorageEntitiesCache storage) {
        this.storageEntitiesCache = storage;
    }

    @Override
    public Iterable<StorageEntityDto> search(SearchCriteria searchCriteria) {
        var query = searchCriteria.getQuery();
        return storageEntitiesCache.getFromCache(query.getValue());
    }

    @Override
    public Iterable<StorageEntityDto> searchAnyWithFilters(FilterCriteria filterCriteria) {
        var collection = storageEntitiesCache.getAllFromCache();

        for(var filter : filterCriteria.getAllFilter())
            filter.apply(collection);

        return collection;
    }

    @Override
    public Iterable<StorageEntityDto> searchWithFilters(SearchCriteria searchCriteria, FilterCriteria filterCriteria) {
        var query = searchCriteria.getQuery();
        var collection = storageEntitiesCache.getFromCache(query.getValue());

        for(var filter : filterCriteria.getAllFilter())
            filter.apply(collection);

        return collection;
    }
}
