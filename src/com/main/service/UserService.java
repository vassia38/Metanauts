package com.main.service;

import com.main.model.User;
import com.main.repository.Repository;

public class UserService {
    public final Repository<Long, User> userRepo;
    public UserService(Repository<Long, User> userRepo){
        this.userRepo = userRepo;
    }

    public User add(User entity){
        return userRepo.save(entity);
    }
    public User delete(User entity){
        return userRepo.delete(entity.getId());
    }
    public User update(User entity){
        return userRepo.update(entity);
    }
    public Iterable<User> getAllEntities(){
        return userRepo.findAll();
    }
    public User findOne(Long id){
        return userRepo.findOne(id);
    }
    public Integer size(){
        return userRepo.size();
    }
}
