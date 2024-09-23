package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.filter.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.filter.SearchCriteria;

public interface StorageEntitySearch {
    Iterable<StorageEntityDto> Search(SearchCriteria criteria);
    StorageEntityDto FindById();
    Iterable<StorageEntityDto> FindByName(String name);
    Iterable<StorageEntityDto> SearchWithFilters(FilterCriteria filters);
}
