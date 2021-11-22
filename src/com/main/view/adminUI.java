package com.main.view;

import com.main.controller.Controller;
import com.main.model.*;
import com.main.service.ServiceException;
import com.main.model.validators.ValidationException;
import com.main.repository.RepositoryException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

/**
 * a basic command line adminUI
 */
public class adminUI extends Thread{
    private static final Map<String, Method> cmdList = new HashMap<>();
    private static Controller controller;
    private static final Scanner keyboard = new Scanner(System.in);
    private User currentUser;
    /**
     * a basic command line adminUI
     * @param controller for friendships and users services
     */
    public adminUI(Controller controller){
        adminUI.controller = controller;
        try {
            cmdList.put("login", adminUI.class.getMethod("loginUser"));
            cmdList.put("logout", adminUI.class.getMethod("logoutUser"));
            cmdList.put("add user", adminUI.class.getMethod("addUser"));
            cmdList.put("delete user", adminUI.class.getMethod("deleteUser"));
            cmdList.put("update user", adminUI.class.getMethod("updateUser"));
            cmdList.put("users", adminUI.class.getMethod("showUsers"));
            cmdList.put("find user", adminUI.class.getMethod("findUserByUsername"));
            cmdList.put("add friendship", adminUI.class.getMethod("addFriendship"));
            cmdList.put("delete friendship", adminUI.class.getMethod("deleteFriendship"));
            cmdList.put("update friendship", adminUI.class.getMethod("updateFriendship"));
            cmdList.put("friendships", adminUI.class.getMethod("showAllFriendships"));
            cmdList.put("communities", adminUI.class.getMethod("showCommunities"));
            cmdList.put("communities max", adminUI.class.getMethod("biggestCommunity"));
            cmdList.put("show friends", adminUI.class.getMethod("showFriends"));
            cmdList.put("show friends in month", adminUI.class.getMethod("showFriendsMonth"));
            cmdList.put("show requests", adminUI.class.getMethod("showRequests"));
            cmdList.put("help", adminUI.class.getMethod("help"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void loginUser(){
        System.out.println("username:");
        String username = keyboard.nextLine();
        User user = controller.findUserByUsername(username);
        if(user != null){
            this.currentUser = user;
            System.out.println("Logged in as " + user.getUsername());
        }
    }
    public void logoutUser(){
        if(this.currentUser == null) {
            System.out.println("Not logged in as a user in admin-mode!\n");
            return;
        }
        System.out.println("Logged out!");
        this.currentUser = null;
    }

    public void addUser(){
        System.out.println("username:");
        String username = keyboard.nextLine();
        System.out.println("First name:");
        String firstName = keyboard.nextLine();
        System.out.println("Last name:");
        String lastName = keyboard.nextLine();
        User user = new User(username, firstName, lastName);
        controller.addUser(user);
    }
    public void deleteUser(){
        if(this.currentUser == null){
            System.out.println("You are not logged in!\n");
            return;
        }
        System.out.println("Are you sure (Y/N) ?");
        String confirm = keyboard.nextLine();
        if(Objects.equals(confirm, "Y") || Objects.equals(confirm, "y")){
            controller.deleteUser(this.currentUser);
            this.currentUser = null;
        }
    }
    public void updateUser(){
        System.out.println(this.currentUser);
        System.out.println("New first name:");
        String firstName = keyboard.nextLine();
        System.out.println("New last name:");
        String lastName = keyboard.nextLine();
        if(firstName.equals("") || lastName.equals(""))
            throw new InputMismatchException("Names can't be empty!");
        controller.updateUser(currentUser,firstName,lastName);
    }
    public void showUsers(){
        for(User u : controller.getAllUsers())
            System.out.println("ID " + u.getId() + " " + u);
    }
    public void findUserByUsername(){
        System.out.println("Username:");
        String username = keyboard.nextLine();
        System.out.println(controller.findUserByUsername(username));
    }

    private Tuple<Long,Long> inputFriendship(){
        try {
            long id1, id2;
            System.out.println("ID user 1:");
            id1 = Long.parseLong(keyboard.nextLine());
            System.out.println("ID user 2:");
            id2 = Long.parseLong(keyboard.nextLine());
            return new Tuple<>(id1, id2);
        }catch(NumberFormatException e){
            throw new InputMismatchException("ID requires a number!");
        }
    }

    public void addFriendship(){
        Tuple<Long,Long> id = inputFriendship();
        Friendship friendship = new Friendship(id.getLeft(),id.getRight());
        controller.addFriendship(friendship);
    }
    public void deleteFriendship(){
        Tuple<Long,Long> id = inputFriendship();
        Friendship friendship = new Friendship(id.getLeft(),id.getRight());
        controller.deleteFriendship(friendship);
    }
    public void updateFriendship(){
        Tuple<Long,Long> id = inputFriendship();
        System.out.println("New date-time (yyyy-MM-dd HH:mm):");
        String dateStr = keyboard.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            Friendship friendship = new Friendship(id.getLeft(),id.getRight(),date);
            controller.updateFriendship(friendship);
        }catch(DateTimeParseException e){
            throw new InputMismatchException("Invalid date format!");
        }
    }
    public void showAllFriendships(){
        for(Friendship fr : controller.getAllFriendships())
            System.out.println(fr);
    }

    public void showCommunities(){
        for(List<Long> idList : controller.getAllCommunities()){
            System.out.println("Community:");
            for(Long id : idList){
                User user = controller.findUserById(id);
                if(user != null)
                    System.out.println(user);
            }
        }
    }
    public void biggestCommunity(){
        System.out.println("Biggest community is made of " +
                controller.getBiggestCommunitySize() + " users");
    }

    private List<Friendship> getFriendshipList() {
        Iterable<Friendship> friendships = controller.getAllFriendships();
        List<Friendship>friendshipList = new ArrayList<>();
        for(Friendship friendship : friendships) {
            friendshipList.add(friendship);
        }
        return friendshipList;
    }

    private void printFriends(Stream<FriendshipDTO> leftFriends, Stream<FriendshipDTO> rightFriends){
        if(rightFriends == null && leftFriends == null) {
            System.out.println("No friends to show :<");
            return;
        }
        if (leftFriends != null) {
            leftFriends.forEach(System.out::println);
        }
        if (rightFriends != null) {
            rightFriends.forEach(System.out::println);
        }
    }

    public void showFriends() {
        System.out.println("username:");
        String username = keyboard.nextLine();

        List<Friendship>friendshipList = getFriendshipList();

        Stream<FriendshipDTO> rightFriends = controller.getRightFriends(controller.findUserByUsername(username),friendshipList);
        Stream<FriendshipDTO> leftFriends = controller.getLeftFriends(controller.findUserByUsername(username),friendshipList);

        printFriends(leftFriends,rightFriends);
    }

    private Month getMonth(String monthString) {
        int monthInt;
        try {
            monthInt = Integer.parseInt(monthString);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Month must be a number!");
        }
        if(monthInt > 12 || monthInt < 1) {
            throw new NumberFormatException("Month must be a number from 1 to 12!");
        }

        return Month.of(monthInt);
    }

    public void showFriendsMonth() {
        System.out.println("username:");
        String username = keyboard.nextLine();
        System.out.println("month (as a number from 1 to 12):");
        String monthString = keyboard.nextLine();

        Month month;

        try {
            month = getMonth(monthString);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return;
        }

        List<Friendship>friendshipList = getFriendshipList();

        Stream<FriendshipDTO> rightFriends = controller.getRightFriendsMonth(
                controller.findUserByUsername(username), month, friendshipList);
        Stream<FriendshipDTO> leftFriends = controller.getLeftFriendsMonth(
                controller.findUserByUsername(username), month, friendshipList);

        printFriends(leftFriends,rightFriends);
    }

    public void showRequests() {
        Iterable<Request> requests = controller.showAllRequests();
        for (Request request : requests) {
            System.out.println(request);
        }
    }

    public void help(){
        if(this.currentUser != null)
            System.out.println("Current user is " + this.currentUser);
        System.out.println("Commands:");
        System.out.println("login = pseudo-login as a user");
        System.out.println("logout = pseudo-logout from a user");
        System.out.println("add user");
        System.out.println("delete user");
        System.out.println("update user");
        System.out.println("users = show all users");
        System.out.println("find user = find user by username");
        System.out.println("add friendship = add friendship");
        System.out.println("delete friendship = delete friendship");
        System.out.println("update friendship = update friendship");
        System.out.println("friendships = show all friendships");
        System.out.println("communities = show all communities");
        System.out.println("communities max = size of biggest community");
        System.out.println("show friends = show all friends of a certain user");
        System.out.println("show friends in month = show all friends a certain user from a certain month");
        System.out.println("show requests = show all friend requests");
        System.out.println("help");
        System.out.println("exit");
    }
    public void start(){
        help();
        String cmd;
        while(true){
            System.out.print(">>>");
            try{
                cmd = keyboard.nextLine();
                if(cmd.equals("exit")){
                    return;
                }
                cmdList.get(cmd).invoke(this);
            }catch(InputMismatchException | NullPointerException ex){
                System.out.println("Wrong input.");
            }catch(ValidationException | RepositoryException | ServiceException ex){
                System.out.println(ex.getMessage());
            }catch(InvocationTargetException ex){
                System.out.println(ex.getCause().getMessage());
            }catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
