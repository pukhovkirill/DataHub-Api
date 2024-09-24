package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.SearchCriteria;

public interface StorageEntitySearch {
    Iterable<StorageEntityDto> Search(SearchCriteria criteria);
    StorageEntityDto FindById();
    Iterable<StorageEntityDto> FindByName(String name);
    Iterable<StorageEntityDto> SearchWithFilters(SearchCriteria criteria, FilterCriteria filters);
}
