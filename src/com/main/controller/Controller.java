package com.main.controller;

import com.main.model.Friendship;
import com.main.model.FriendshipDTO;
import com.main.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface Controller {
    void addUser(User entity);
    User deleteUser(User entity);
    User updateUser(User entity, String firstName, String lastName);
    User findUserById(Long id);
    User findUserByUsername(String username);
    Iterable<User> getAllUsers();
    void addFriendship(Friendship entity);
    Friendship deleteFriendship(Friendship entity);
    Friendship updateFriendship(Friendship entity);
    Iterable<Friendship> getAllFriendships();
    ArrayList<ArrayList<Long>> getAllCommunities();
    int getBiggestCommunitySize();
    public Stream<FriendshipDTO> getRightFriends(User user, List<Friendship> friendshipList);
    public Stream<FriendshipDTO> getLeftFriends(User user, List<Friendship> friendshipList);

}
