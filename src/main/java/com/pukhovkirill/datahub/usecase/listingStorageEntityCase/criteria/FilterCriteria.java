package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria;

import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.filter.Filter;

public interface FilterCriteria {
    void addFilter(Filter filter);
    Iterable<Filter> getAllFilter(Filter filter);
}
