package com.main.controller;

import com.main.algo.Graph;
import com.main.model.Friendship;
import com.main.model.FriendshipDTO;
import com.main.model.User;
import com.main.repository.RepositoryException;
import com.main.service.FriendshipService;
import com.main.service.MessageService;
import com.main.service.UserService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ControllerClass implements Controller{
    public final UserService userService;
    public final  FriendshipService friendshipService;
    public final MessageService messageService;
    Graph graph;

    public ControllerClass(UserService userService, FriendshipService friendshipService,
                           MessageService messageService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    /**
     * add User(username,firstname,lastname)
     * @param entity user
     * @throws RepositoryException if user with same (id and ) username already exists
     */
    @Override
    public void addUser(User entity) {
        User user = userService.add(entity);
        if(user != null)
            throw new RepositoryException("User already exists!\n");
    }

    /**
     * delete logged-in user and its friendships
     * @param entity user
     * @return old state of entity
     * @throws RepositoryException if user was not found
     */
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

    /**
     * update logged-in user
     * @param entity user
     * @param firstName new first name
     * @param lastName new last name
     * @return old state of entity
     * @throws RepositoryException if user was not found
     */
    @Override
    public User updateUser(User entity, String firstName, String lastName){
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        User oldState = userService.update(entity);
        if(oldState == null)
            throw new RepositoryException("User doesn't exist!\n");
        return oldState;
    }

    /**
     * find user by given id
     * @param id Long
     * @return user if found
     * @throws RepositoryException if user was not found
     */
    @Override
    public User findUserById(Long id) {
        User user = userService.findOneById(id);
        if(user == null)
            throw new RepositoryException("User doesn't exist!\n");
        return user;
    }

    /**
     * find user by given username
     * @param username String
     * @return user if found
     * @throws RepositoryException if user was not found
     */
    @Override
    public User findUserByUsername(String username) {
        User user = userService.findOneByUsername(username);
        if(user == null)
            throw new RepositoryException("User doesn't exist!\n");
        return user;
    }

    /**
     * get iterable list of all existing users
     * @return iterable of users
     */
    @Override
    public Iterable<User> getAllUsers() {
        return userService.getAllEntities();
    }

    /**
     * add a friendship
     * @param entity friendship composed of 2 user IDs
     * @throws RepositoryException if either ID doesn't match an user
     * @throws RepositoryException if friendship between the 2 users already
     * exists
     */
    @Override
    public void addFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOneById(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOneById(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship fr = friendshipService.add(entity);
        if(fr != null)
            throw new RepositoryException("Friendship already exists!\n");
    }

    /**
     * delete friendship between 2 users, by given IDs
     * @param entity friendship composed of 2 user IDs
     * @return old state of friendship
     * @throws RepositoryException if either ID doesn't match an user
     * @throws RepositoryException if friendship between the 2 users doesn't
     * exist
     */
    @Override
    public Friendship deleteFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOneById(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOneById(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship fr = friendshipService.delete(entity);
        if(fr == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        return fr;
    }

    /**
     * update state of friendship between 2 users, by given IDs
     * @param entity friendship composed of 2 IDs
     * @return old state of friendship
     * @throws RepositoryException if friendship between the 2 users doesn't
     * exist
     */
    @Override
    public Friendship updateFriendship(Friendship entity){
        Friendship oldState = friendshipService.update(entity);
        if(oldState == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        return oldState;
    }

    /**
     *  get an iterable list of all friendships existing
     * @return iterable of friendships
     */
    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipService.getAllEntities();
    }

    /**
     * draw graph of all existing friendships
     */
    private void runGraph(){
        graph = new Graph(this);
        graph.runConnectedComponents();
    }

    /**
     * get list of all communities existing. community = connected component
     * of users
     * @return list of IDs list
     */
    @Override
    public ArrayList<ArrayList<Long>> getAllCommunities(){
        this.runGraph();
        return graph.getCommunities();
    }

    /**
     * get size of biggest existing community
     * @return size
     */
    @Override
    public int getBiggestCommunitySize(){
        this.runGraph();
        return graph.maxSize();
    }

    @Override
    public Stream<FriendshipDTO> getRightFriends(User user, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getLeft().equals(user.getId());
        return friendshipList.stream().filter(friends).map(x ->
                new FriendshipDTO(findUserById(x.getId().getRight()).getLastName(),
                        findUserById(x.getId().getRight()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getLeftFriends(User user, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getRight().equals(user.getId());
        return friendshipList.stream().filter(friends).map(x ->
                new FriendshipDTO(findUserById(x.getId().getLeft()).getLastName(),
                        findUserById(x.getId().getLeft()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getRightFriendsMonth(User user, Month month, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getLeft().equals(user.getId());
        Predicate<Friendship> friendsMonth = x -> x.getDate().getMonth() == month;
        Predicate<Friendship> filtered = friends.and(friendsMonth);
        return friendshipList.stream().filter(filtered).map(x ->
                new FriendshipDTO(findUserById(x.getId().getRight()).getLastName(),
                        findUserById(x.getId().getRight()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getLeftFriendsMonth(User user, Month month, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getRight().equals(user.getId());
        Predicate<Friendship> friendsMonth = x -> x.getDate().getMonth() == month;
        Predicate<Friendship> filtered = friends.and(friendsMonth);
        return friendshipList.stream().filter(filtered).map(x ->
                new FriendshipDTO(findUserById(x.getId().getLeft()).getLastName(),
                        findUserById(x.getId().getLeft()).getFirstName(), x.getDate()));
    }

    @Override
    public void sendMessage(User source, List<User> destination, String message, LocalDateTime date, Long repliedMessageId) {

    }


}
