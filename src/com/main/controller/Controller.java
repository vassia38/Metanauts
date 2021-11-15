package com.main.controller;

import com.main.model.Friendship;
import com.main.model.User;
import com.main.service.FriendshipService;
import com.main.service.UserService;

import java.util.ArrayList;

public interface Controller {
    void addUser(User entity);
    User deleteUser(User entity);
    User updateUser(User entity);
    User findUserById(Long id);
    Iterable<User> getAllUsers();
    void addFriendship(Friendship entity);
    Friendship deleteFriendship(Friendship entity);
    Friendship updateFriendship(Friendship entity);
    Iterable<Friendship> getAllFriendships();
    ArrayList<ArrayList<Long>> getAllCommunities();
    int getBiggestCommunitySize();
}
