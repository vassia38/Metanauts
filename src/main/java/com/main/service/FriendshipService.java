package com.main.service;

import com.main.model.Friendship;
import com.main.model.Tuple;
import com.main.repository.Repository;

public class FriendshipService {
    final Repository<Tuple<Long,Long>, Friendship> friendshipRepository;

    public FriendshipService(Repository<Tuple<Long,Long>, Friendship> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }
    public Friendship add(Friendship entity){
        return friendshipRepository.save(entity);
    }

    public Friendship delete(Friendship entity){
        return friendshipRepository.delete(entity.getId());
    }

    public Friendship update(Friendship entity){
        return friendshipRepository.update(entity);
    }

    public Iterable<Friendship> getAllEntities(){
        return friendshipRepository.findAll();
    }

    public Iterable<Friendship> getFriendships(Tuple<Long,Long> id){
        return friendshipRepository.findAll(id);
    }

    public Friendship findFriendshipById(Tuple<Long, Long> id) { return friendshipRepository.findOneById(id); }
}
