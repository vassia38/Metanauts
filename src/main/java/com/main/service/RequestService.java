package com.main.service;

import com.main.model.Request;
import com.main.model.Tuple;
import com.main.repository.Repository;

import java.util.ArrayList;

public class RequestService{
    final Repository<Tuple<Long,Long>, Request> requestRepository;

    public RequestService(Repository<Tuple<Long,Long>, Request> requestRepository) {
        this.requestRepository = requestRepository;
    }
    public Request add(Request entity){
        Request req = requestRepository.save(entity);
        return req;
    }

    public Request delete(Request entity){
        Request req = requestRepository.delete(entity.getId());
        return req;
    }

    public Request update(Request entity){
        Request oldReq = requestRepository.update(entity);
        return oldReq;
    }

    public Iterable<Request> getAllEntities(){
        return requestRepository.findAll();
    }

    public Request findOneById(Tuple<Long,Long> id) {
        return requestRepository.findOneById(id);
    }

}
