package com.es2.vadebicicleta.externo.commons.repository.manager;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LongIdManager {
    private static Map<Class<?>, Long> idCounters;

    public LongIdManager(){
        idCounters = new HashMap<>();
    }

    public Long generateId(Class<?> repository, LongGenerationStrategy strategy){
        if(LongGenerationStrategy.IDENTITY.equals(strategy)){
            return identity(repository);
        }
        else{
            return null;
        }
    }

    private Long identity(Class<?> repository){
       Long idCounter = idCounters.get(repository);
       if(idCounter == null){
           idCounter = 0L;

       }else {
           idCounter++;
       }
       idCounters.put(repository, idCounter);

       return idCounter;
    }

    public enum LongGenerationStrategy{
        IDENTITY
    }

}
