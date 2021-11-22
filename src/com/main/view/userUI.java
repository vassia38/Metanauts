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
import java.util.*;
import java.util.stream.Stream;

/**
 * a basic command line userUI
 */
public class userUI extends Thread{
    private static final Map<String, Method> cmdList = new HashMap<>();
    private static Controller controller;
    private static final Scanner keyboard = new Scanner(System.in);
    private User currentUser;
    /**
     * a basic command line userUI
     * @param controller for friendships and users services
     */
    public userUI(Controller controller, String user){
        userUI.controller = controller;
        currentUser = controller.findUserByUsername(user);
        try {
            cmdList.put("message",userUI.class.getMethod("sendMessage"));
            cmdList.put("find conversation",userUI.class.getMethod("findConversation"));
            cmdList.put("delete user", userUI.class.getMethod("deleteUser"));
            cmdList.put("update user", userUI.class.getMethod("updateUser"));
            cmdList.put("users", userUI.class.getMethod("showUsers"));
            cmdList.put("find user", userUI.class.getMethod("findUserByUsername"));
            cmdList.put("add friendship", userUI.class.getMethod("addFriendship"));
            cmdList.put("delete friendship", userUI.class.getMethod("deleteFriendship"));
            cmdList.put("friendships", userUI.class.getMethod("showAllFriendships"));
            cmdList.put("communities", userUI.class.getMethod("showCommunities"));
            cmdList.put("communities max", userUI.class.getMethod("biggestCommunity"));
            cmdList.put("show friends", userUI.class.getMethod("showFriends"));
            cmdList.put("show friends in month", userUI.class.getMethod("showFriendsMonth"));
            cmdList.put("help", userUI.class.getMethod("help"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        System.out.println("""
                Type usernames separated by ENTER.
                Leave empty and press ENTER again when done.
                Destinations:""");
        List<String> usernames =new ArrayList<>();
        String str;
        str = keyboard.nextLine();
        while(!str.equals("")){
            usernames.add(str);
            str = keyboard.nextLine();
        }
        System.out.println("Replying to (type message id, leave empty if not):");
        long repliedId = 0;
        str = keyboard.nextLine();
        try{
            repliedId = Long.parseLong(str);
        }catch(NumberFormatException e){
            if(!Objects.equals(str, ""))
                throw new InputMismatchException("ID requires a number!");
        }
        System.out.println("Message (press ENTER = send):");
        String messageText = keyboard.nextLine();
        controller.sendMessage(currentUser,usernames,messageText, LocalDateTime.now(), repliedId);
    }

    public void findConversation(){
        System.out.println("Username of other participant:");
        String username = keyboard.nextLine();
        for(Message m : controller.getConversation(currentUser.getUsername(), username)){
            System.out.println(m);
        }
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
            System.out.println("No friends to show! :<");
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
        List<Friendship>friendshipList = getFriendshipList();

        Stream<FriendshipDTO> rightFriends = controller.getRightFriends(currentUser,friendshipList);
        Stream<FriendshipDTO> leftFriends = controller.getLeftFriends(currentUser,friendshipList);

        printFriends(leftFriends, rightFriends);
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
        System.out.println("month (as number):");
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
                currentUser, month, friendshipList);
        Stream<FriendshipDTO> leftFriends = controller.getLeftFriendsMonth(
                currentUser, month, friendshipList);

        printFriends(leftFriends,rightFriends);
    }

    public void help(){
        if(this.currentUser != null)
            System.out.println("Current user is " + this.currentUser);
        System.out.println("Commands:");
        System.out.println("logout / exit");
        System.out.println("message = write and send a message to multiple users");
        System.out.println("find conversation");
        System.out.println("delete user");
        System.out.println("update user");
        System.out.println("users = show all users");
        System.out.println("find user = find user by username");
        System.out.println("add friendship = add friendship");
        System.out.println("delete friendship = delete friendship");
        System.out.println("friendships = show all friendships");
        System.out.println("communities = show all communities");
        System.out.println("communities max = size of biggest community");
        System.out.println("show friends = show all friends of the current user");
        System.out.println("show friends in month = show all friends of the current user from a certain month");
        System.out.println("help");
    }
    public void start(){
        help();
        String cmd;
        while(true){
            System.out.print(">>>");
            try{
                cmd = keyboard.nextLine();
                if(cmd.equals("exit") || cmd.equals("logout")){
                    System.out.println("Logged out!");
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
