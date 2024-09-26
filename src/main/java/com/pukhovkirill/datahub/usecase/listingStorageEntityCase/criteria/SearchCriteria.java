package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria;

import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.search.Query;

public interface SearchCriteria {
    boolean setQuery(Query query);
    Query getQuery();
}
