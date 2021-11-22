package com.main.controller;

import com.main.model.*;

import java.time.Month;
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
    Stream<FriendshipDTO> getRightFriends(User user, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getLeftFriends(User user, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getRightFriendsMonth(User user, Month month, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getLeftFriendsMonth(User user, Month month, List<Friendship> friendshipList);
    public void addRequest(Request request);
    public void answerRequest(Request request, String answer);
    public Iterable<Request> showRequests(User user);
    public Iterable<Request> showAllRequests();
    public Friendship findFriendshipById(Tuple<Long,Long> id);
}
