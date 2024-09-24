package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.filter;

public interface Filter {
    void setName(String name);
    void setType(FilterType type);
    void setValue(String value);
}
