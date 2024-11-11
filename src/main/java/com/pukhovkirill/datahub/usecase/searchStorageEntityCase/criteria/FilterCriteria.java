package com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.filter.Filter;

public interface FilterCriteria {
    boolean addFilter(Filter<?,StorageEntityDto> filter);
    Iterable<Filter<?,StorageEntityDto>> getAllFilter();
}
