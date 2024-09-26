package com.pukhovkirill.datahub.infrastructure.collection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntityArrayTire implements Tire<StorageEntityDto>{

    private volatile TireNode root;

    private final Lock writeLock = new ReentrantLock();

    public StorageEntityArrayTire() {
        root = new TireNode();
    }

    private static class TireNode {
        private final AtomicReferenceArray<TireNode> outgoingNodes;

        private final List<StorageEntityDto> entities;

        public TireNode() {
            this.outgoingNodes = new AtomicReferenceArray<>(new TireNode[26]);
            this.entities = new CopyOnWriteArrayList<>();
        }

        public TireNode getChild(char ch){
            return this.outgoingNodes.get(ch - 'a');
        }

        public synchronized void setChild(char ch){
            this.outgoingNodes.set(ch - 'a', new TireNode());
        }

        public Collection<StorageEntityDto> getEntities(){
            return this.entities;
        }

        public synchronized void addEntity(StorageEntityDto entity){
            if(entity == null) return;
            this.entities.add(entity.clone());
        }

        public synchronized void removeEntity(StorageEntityDto entity){
            entities.removeIf(entity::equals);
        }
    }

    protected void acquireWriteLock() {
        writeLock.lock();
    }

    protected void releaseWriteLock() {
        writeLock.unlock();
    }

    @Override
    public void add(StorageEntityDto entity) {
        acquireWriteLock();
        try{
            String name = entity.getName();
            var currentNode = this.root;
            for(char ch : name.toCharArray()){
                if (currentNode.getChild(ch) == null){
                    currentNode.setChild(ch);
                }
                currentNode = currentNode.getChild(ch);
            }
            currentNode.addEntity(entity);
        }finally{
            releaseWriteLock();
        }
    }

    @Override
    public StorageEntityDto find(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lazyErase(StorageEntityDto entity) {
        var currentNode = this.root;
        var name = entity.getName();
        for(char ch : name.toCharArray()){
            if (currentNode.getChild(ch) == null){
                return;
            }
            currentNode = currentNode.getChild(ch);
        }

        currentNode.removeEntity(entity);
    }

    @Override
    public Collection<StorageEntityDto> findFuzzy(String name) {
        Collection<StorageEntityDto> entities = new LinkedList<>();
        var currentNode = this.root;
        for(char ch : name.toCharArray()){
            if(currentNode.getChild(ch) != null){
                for(var entity : currentNode.getEntities()){
                    entities.add(entity.clone());
                }
            }
            if (currentNode.getChild(ch) == null){
                return entities;
            }
            currentNode = currentNode.getChild(ch);
        }

        return entities;
    }

    @Override
    public void fill(Iterable<StorageEntityDto> entities){
        acquireWriteLock();
        try{
            for(StorageEntityDto entity : entities){
                var currentNode = this.root;
                String name = entity.getName();
                for(char ch : name.toCharArray()){
                    if (currentNode.getChild(ch) == null){
                        currentNode.setChild(ch);
                    }
                    currentNode = currentNode.getChild(ch);
                }
                currentNode.addEntity(entity);
            }
        }finally{
            releaseWriteLock();
        }
    }
}
