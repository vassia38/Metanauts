package com.main.service;

import com.main.model.Friendship;
import com.main.model.Tuple;
import com.main.repository.Repository;

public class FriendshipService {
    final Repository<Tuple<Long,Long>, Friendship> friendshipRepository;

    public FriendshipService(Repository<Tuple<Long,Long>, Friendship> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
        /* keep for backwards compatibility
        for(Friendship fr : friendshipRepository.findAll()){
                this.add(fr);
        }*/
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
}
