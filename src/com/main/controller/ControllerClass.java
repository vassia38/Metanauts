package com.main.controller;

import com.main.algo.Graph;
import com.main.model.Friendship;
import com.main.model.User;
import com.main.repository.RepositoryException;
import com.main.service.FriendshipService;
import com.main.service.UserService;

import java.util.ArrayList;
import java.util.Objects;

public class ControllerClass implements Controller{
    public final UserService userService;
    public final  FriendshipService friendshipService;
    Graph graph;

    public ControllerClass(UserService userService, FriendshipService friendshipService){
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @Override
    public void addUser(User entity) {
        User user = userService.add(entity);
        if(user != null)
            throw new RepositoryException("User already exists!\n");
    }

    @Override
    public User deleteUser(User entity) {
        User deleted = userService.delete(entity);
        if(deleted == null)
            throw new RepositoryException("User doesn't exist!\n");
        ArrayList<Friendship> list = new ArrayList<>();
        for (Friendship fr : friendshipService.getAllEntities()) {
            list.add(fr);
        }
        for(Friendship fr : list){
            if (Objects.equals(fr.getId().getLeft(), entity.getId()) ||
                    Objects.equals(fr.getId().getRight(), entity.getId())) {
                friendshipService.delete(fr);
            }
        }
        return deleted;
    }

    @Override
    public User updateUser(User entity){
        User oldState = userService.update(entity);
        if(oldState == null)
            throw new RepositoryException("User doesn't exist!\n");
        return oldState;
    }

    @Override
    public User findUserById(Long id) {
        User user = userService.findOne(id);
        if(user == null)
            throw new RepositoryException("User doesn't exist!\n");
        return user;
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userService.getAllEntities();
    }

    @Override
    public void addFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOne(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOne(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship fr = friendshipService.add(entity);
        if(fr != null)
            throw new RepositoryException("Friendship already exists!\n");
    }

    @Override
    public Friendship deleteFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOne(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOne(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship fr = friendshipService.delete(entity);
        if(fr == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        return fr;
    }

    @Override
    public Friendship updateFriendship(Friendship entity){
        Friendship oldState = friendshipService.update(entity);
        if(oldState == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        return oldState;
    }

    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipService.getAllEntities();
    }
    private void runGraph(){
        graph = new Graph(this);
        graph.runConnectedComponents();
    }
    @Override
    public ArrayList<ArrayList<Long>> getAllCommunities(){
        this.runGraph();
        return graph.getCommunities();
    }
    @Override
    public int getBiggestCommunitySize(){
        this.runGraph();
        return graph.maxSize();
    }
}
