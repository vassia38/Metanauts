package com.main.service;

import com.main.model.User;
import com.main.repository.db.UserDbRepository;

public class UserService {
    private final UserDbRepository userRepo;
    public UserService(UserDbRepository userRepo){
        this.userRepo = userRepo;
    }

    public User add(User entity, String salt){
        return userRepo.saveWithSalt(entity, salt);
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
    public User findOneById(Long id){
        return userRepo.findOneById(id);
    }
    public User findOneByUsername(String username){
        return userRepo.findOneByUsername(username);
    }
    public Integer size(){
        return userRepo.size();
    }
    public String getSalt(String username) {
        return userRepo.getSalt(username);
    }
}
