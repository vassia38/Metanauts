package com.main.controller;


import com.main.model.*;
import com.main.service.*;
import com.main.utils.observer.Observable;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface Controller extends Observable {
    UserService getUserService();
    FriendshipService getFriendshipService();
    MessageService getMessageService();
    RequestService getRequestService();
    GroupService getGroupService();

    // USERS
    void addUser(User entity);
    User deleteUser(User entity);
    User updateUser(User entity, String firstName, String lastName);
    User findUserById(Long id);
    User findUserByUsername(String username);
    Iterable<User> getAllUsers();

    // FRIENDS
    void addFriendship(Friendship entity);
    Friendship deleteFriendship(Friendship entity);
    Friendship updateFriendship(Friendship entity);
    Iterable<Friendship> getAllFriendships();
    List<User> getAllFriends(User user);
    ArrayList<ArrayList<Long>> getAllCommunities();
    int getBiggestCommunitySize();
    Stream<FriendshipDTO> getRightFriends(User user, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getLeftFriends(User user, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getRightFriendsMonth(User user, Month month, List<Friendship> friendshipList);
    Stream<FriendshipDTO> getLeftFriendsMonth(User user, Month month, List<Friendship> friendshipList);

    // MESSAGES
    Iterable<Message> getAllMesagesOfUser(String username);
    void sendMessage(User source, List<String> destinationUsernames, String message, LocalDateTime date, Long repliedMessageId);
    Iterable<Message> getConversation(String username1, String username2);

    // GROUPS & GROUP MESSAGES
    void createGroup(String nameGroup, List<User> users);
    void sendGroupMessage(User source, Long idGroup, String message, LocalDateTime date, Long repliedMessageId);
    void sendGroupMessage(User source, Long idGroup, String message, LocalDateTime date);
    void addMemberToGroup(Group group, User user);
    Iterable<GroupMessage> getGroupConversation(User user, String nameGroup);
    Group findGroupByName(String name, User user);
    Iterable<Group> getAllGroups(User user);

    // REQUESTS
    void addRequest(Request request);
    void answerRequest(Request request, String answer);
    Iterable<Request> getAllRequests(User user);
    Iterable<Request> showAllRequests();
    Friendship findFriendshipById(Tuple<Long,Long> id);
    Request findRequest(Request request);
    Request deleteRequest(Request request);
    Iterable<Request> getAllAnsweredRequests(User user);
}
