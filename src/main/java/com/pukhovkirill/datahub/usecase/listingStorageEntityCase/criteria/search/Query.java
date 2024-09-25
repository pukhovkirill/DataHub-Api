package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.search;

public interface Query {
    void setValue(String query);
    String getValue();
}
