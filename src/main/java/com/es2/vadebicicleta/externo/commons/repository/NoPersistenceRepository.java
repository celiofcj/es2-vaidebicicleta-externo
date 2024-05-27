package com.es2.vadebicicleta.externo.commons.repository;

import com.es2.vadebicicleta.externo.commons.repository.manager.LongIdManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class NoPersistenceRepository<T extends Identificable<Long>> {
    private LongIdManager longIdManager;
    private Map<Long, T> elements;

    @Autowired
    protected NoPersistenceRepository(LongIdManager longIdManager) {
        this.longIdManager = longIdManager;
        this.elements = new HashMap<>();
    }

    public T save(T o){
        if(o.getId() == null){
            Long id = longIdManager.generateId(getClass(), LongIdManager.LongGenerationStrategy.IDENTITY);
            o.setId(id);
        }
        else {
            Optional<T> saved = findById(o.getId());

            if(saved.isEmpty()){
                throw new ElementNotFoundException("Not possible to update element with id: " +
                        o.getId() + ". Does not exists element with this id");
            }
        }

        elements.put(o.getId(), o);
        return o;
    }

    public Iterable<T> saveAll(Iterable<T> entities){
        Iterator<T> it = entities.iterator();
        List<T> returnList = new ArrayList<>();
        while(it.hasNext()){
            T saved = save(it.next());
            returnList.add(saved);
        }

        return returnList;
    }

    public Optional<T> findById(Long id){
        T element = elements.get(id);

        if(element == null){
            return Optional.empty();
        }

        return Optional.of(element);
    }

    public boolean existsById(Long id){
        return elements.containsKey(id);
    }

    public Iterable<T> findAll() {
        return new ArrayList<>(elements.values());
    }

    public Iterable<T> findAllById(Iterable<Long> ids){
        Iterator<Long> it = ids.iterator();
        List<T> returnList = new ArrayList<>();
        while(it.hasNext()){
            Long next = it.next();
            T element = elements.get(next);
            if(element != null){
                returnList.add(element);
            }
        }

        return returnList;
    }

    public long count(){
        return elements.size();
    }

    public void deleteById(Long id){
        elements.remove(id);
    }

    public void delete(T o){
        elements.remove(o.getId());
    }

    public void deleteAllById(Iterable<Long> ids) {
        ids.forEach(this::deleteById);
    }

    public void deleteAll(Iterable<T> entities) {
        entities.forEach(this::delete);
    }

    public void deleteAll(){
        elements.clear();
    }

}
