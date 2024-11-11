package com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria;

import com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.search.Query;

public interface SearchCriteria {
    boolean setQuery(Query query);
    Query getQuery();
}
