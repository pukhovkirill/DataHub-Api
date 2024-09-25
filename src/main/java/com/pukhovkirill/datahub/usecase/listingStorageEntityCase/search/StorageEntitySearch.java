package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.SearchCriteria;

public interface StorageEntitySearch extends Cloneable{
    Iterable<StorageEntityDto> search(SearchCriteria criteria);
    Iterable<StorageEntityDto> searchAnyWithFilters(FilterCriteria filters);
    Iterable<StorageEntityDto> searchWithFilters(SearchCriteria criteria, FilterCriteria filters);
}
