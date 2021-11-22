package com.main.service;

import com.main.model.Request;
import com.main.model.Tuple;
import com.main.repository.Repository;

public class RequestService {
    final Repository<Tuple<Long,Long>, Request> requestRepository;

    public RequestService(Repository<Tuple<Long,Long>, Request> requestRepository) {
        this.requestRepository = requestRepository;
    }
    public Request add(Request entity){
        return requestRepository.save(entity);
    }

    public Request delete(Request entity){
        return requestRepository.delete(entity.getId());
    }

    public Request update(Request entity){
        return requestRepository.update(entity);
    }

    public Iterable<Request> getAllEntities(){
        return requestRepository.findAll();
    }

    public Request findOneById(Tuple<Long,Long> id) { return requestRepository.findOneById(id); }
}
