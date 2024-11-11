package com.pukhovkirill.datahub.usecase.searchStorageEntityCase.search;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.SearchCriteria;

public interface StorageEntitySearch extends Cloneable{
    Iterable<StorageEntityDto> search(SearchCriteria searchCriteria);
    Iterable<StorageEntityDto> searchAnyWithFilters(FilterCriteria filterCriteria);
    Iterable<StorageEntityDto> searchWithFilters(SearchCriteria searchCriteria, FilterCriteria filterCriteria);
}
