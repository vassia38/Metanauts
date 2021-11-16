package com.main.view;

import com.main.controller.Controller;
import com.main.service.ServiceException;
import com.main.model.Friendship;
import com.main.model.User;
import com.main.model.validators.ValidationException;
import com.main.repository.RepositoryException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * a basic command line UI
 */
public class UI extends Thread{
    private static final Map<String, Method> cmdList = new HashMap<>();
    private static Controller controller;
    private static final Scanner keyboard = new Scanner(System.in);
    private User currentUser;
    /**
     * a basic command line UI
     * @param controller for friendships and users services
     */
    public UI(Controller controller){
        UI.controller = controller;
        try {
            cmdList.put("login", UI.class.getMethod("loginUser"));
            cmdList.put("logout", UI.class.getMethod("logoutUser"));
            cmdList.put("add user",UI.class.getMethod("addUser"));
            cmdList.put("delete user",UI.class.getMethod("deleteUser"));
            cmdList.put("update user",UI.class.getMethod("updateUser"));
            cmdList.put("users",UI.class.getMethod("showUsers"));
            cmdList.put("find user",UI.class.getMethod("findUserByUsername"));
            cmdList.put("add friendship",UI.class.getMethod("addFriendship"));
            cmdList.put("delete friendship",UI.class.getMethod("deleteFriendship"));
            cmdList.put("update friendship",UI.class.getMethod("updateFriendship"));
            cmdList.put("friendships",UI.class.getMethod("showAllFriendships"));
            cmdList.put("communities",UI.class.getMethod("showCommunities"));
            cmdList.put("communities max",UI.class.getMethod("biggestCommunity"));
            cmdList.put("help",UI.class.getMethod("help"));
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
        if(this.currentUser != null){
            System.out.println("Logged out!");
            this.currentUser = null;
        }
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
        System.out.println("Are you sure (Y/N) ?");
        String confirm = keyboard.nextLine();
        if(Objects.equals(confirm, "Y") || Objects.equals(confirm, "y")){
            controller.deleteUser(this.currentUser);
            this.currentUser = null;
        }
    }
    public void updateUser(){
        System.out.println("New first name:");
        keyboard.nextLine();
        String firstName = keyboard.nextLine();
        System.out.println("New last name:");
        String lastName = keyboard.nextLine();

        controller.updateUser(currentUser,firstName,lastName);
    }
    public void showUsers(){
        for(Object u : controller.getAllUsers())
            System.out.println(u);
    }
    public void findUserByUsername(){
        System.out.println("Username:");
        String username = keyboard.nextLine();
        System.out.println(controller.findUserByUsername(username));
    }

    public void addFriendship(){
        System.out.println("ID user 1:");
        keyboard.nextLine();
        Long ID1 = keyboard.nextLong();
        System.out.println("ID user 2:");
        keyboard.nextLine();
        Long ID2 = keyboard.nextLong();
        Friendship friendship = new Friendship(ID1,ID2);
        controller.addFriendship(friendship);
    }
    public void deleteFriendship(){
        System.out.println("ID user 1:");
        keyboard.nextLine();
        Long ID1 = keyboard.nextLong();
        System.out.println("ID user 2:");
        keyboard.nextLine();
        Long ID2 = keyboard.nextLong();
        Friendship friendship = new Friendship(ID1,ID2);
        controller.deleteFriendship(friendship);
    }
    public void updateFriendship(){
        System.out.println("ID user 1:");
        keyboard.nextLine();
        Long ID1 = keyboard.nextLong();
        System.out.println("ID user 2:");
        keyboard.nextLine();
        Long ID2 = keyboard.nextLong();
        System.out.println("New date-time (yyyy-MM-dd HH:mm):");
        keyboard.nextLine();
        String dateStr = keyboard.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            Friendship friendship = new Friendship(ID1,ID2,date);
            controller.updateFriendship(friendship);
        }catch(DateTimeParseException e){
            throw new InputMismatchException();
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

    public void help(){
        if(this.currentUser != null)
            System.out.println("Current user is " + this.currentUser);
        System.out.println("Commands:");
        System.out.println("login");
        System.out.println("logout");
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
                if(Objects.equals(cmd, "exit")){
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
