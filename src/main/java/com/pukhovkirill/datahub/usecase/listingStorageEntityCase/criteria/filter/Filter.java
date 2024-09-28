package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.filter;

import java.util.Collection;

public interface Filter<K,E> {
    void setType(FilterType type);
    void setValue(K value);

    void apply(Collection<E> collection);
}
