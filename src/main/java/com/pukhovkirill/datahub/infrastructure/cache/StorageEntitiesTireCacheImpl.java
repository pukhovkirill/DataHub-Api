package com.pukhovkirill.datahub.infrastructure.cache;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;

public class StorageEntitiesTireCacheImpl implements StorageEntitiesCache {

    //todo: implement concurrent tire collection
    private static final TireNode root;

    static{
        root = new TireNode();
    }

    @Setter
    private static class TireNode{
        private final TireNode[] children;
        private StorageEntityDto entity;

        public TireNode() {
            this.children = new TireNode[26];
            this.entity = null;
        }
    }

    private void add(TireNode node, StorageEntityDto entity){
        String path = entity.getPath();

        for(char ch : path.toCharArray()){
            if (node.children[ch - 'a'] == null){
                node.children[ch - 'a'] = new TireNode();
            }
            node = node.children[ch - 'a'];
        }
        node.entity = entity;
    }

    private Collection<StorageEntityDto> find(TireNode node, String path) {
        Collection<StorageEntityDto> entities = new LinkedList<>();
        for(char ch : path.toCharArray()){
            if(node.entity != null) entities.add(node.entity.clone());
            if (node.children[ch - 'a'] == null){
                return entities;
            }
            node = node.children[ch - 'a'];
        }

        return entities;
    }

    private void fillTire(Iterable<StorageEntityDto> entities){
        for(StorageEntityDto entity : entities)
            add(root, entity);
    }

    @Override
    public Iterable<StorageEntityDto> getFromCache(String key) {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> getAllFromCache() {
        return null;
    }

    @Override
    public void saveToCache(String key, StorageEntityDto value) {

    }

    @Override
    public void removeFromCache(String key) {

    }

    @Override
    public boolean hasInCache(String key) {
        return false;
    }

    @Override
    public void clearCache() {

    }
}
