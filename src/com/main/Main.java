package com.main;

import com.main.controller.Controller;
import com.main.controller.ControllerClass;
import com.main.model.Message;
import com.main.model.User;
import com.main.model.validators.MessageValidator;
import com.main.repository.RepositoryException;
import com.main.repository.db.FriendshipDbRepository;
import com.main.repository.db.MessageDbRepository;
import com.main.repository.db.UserDbRepository;
import com.main.service.FriendshipService;
import com.main.service.UserService;
import com.main.model.validators.FriendshipValidator;
import com.main.model.validators.UserValidator;
import com.main.view.adminUI;
import com.main.view.userUI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";
        UserValidator userValidator = new UserValidator();

        FriendshipValidator friendshipValidator = new FriendshipValidator();
        UserDbRepository userRepo = new UserDbRepository(
                url, username,password, userValidator);
        FriendshipDbRepository friendshipRepo =
                new FriendshipDbRepository(url, username, password, friendshipValidator);
        UserService userService =
                new UserService(userRepo);
        FriendshipService friendshipService =
                new FriendshipService(friendshipRepo);
        Controller controller =
                new ControllerClass(userService,friendshipService);
        Scanner keyboard = new Scanner(System.in);


        /*List<User> all = new ArrayList<>();
        all.add(controller.findUserById(2L));
        all.add(controller.findUserById(4L));
        all.add(controller.findUserById(6L));
        MessageValidator messageValidator = new MessageValidator();
        MessageDbRepository messageRepo = new MessageDbRepository(url,username,password, messageValidator);
        Message msg1 = new Message(1L,controller.findUserById(3L),
                all, "Hello all!", LocalDateTime.now());
        messageRepo.save(msg1);
        all.add(controller.findUserById(3L));
        all.remove(controller.findUserById(2L));
        all.remove(controller.findUserById(4L));
        all.remove(controller.findUserById(6L));
        Message msg2 = new Message(2L, controller.findUserById(7L),
                all, "Hi vasi!", LocalDateTime.now(), msg1);
        messageRepo.save(msg2);

        msg1 = messageRepo.findMessageById(1L);
        msg2 = messageRepo.findMessageById(2L);
        System.out.println(msg1);
        System.out.println(msg2);*/

        while(true){
            System.out.println("Available actions:");
            System.out.println("1. login");
            System.out.println("2. exit");
            System.out.print(">>>");
            String cmd = keyboard.nextLine();
            if(cmd.equals("exit")){
                break;
            }
            if(cmd.equals("login")){
                System.out.println("Username:");
                cmd = keyboard.nextLine();
                if(cmd.equals("admin")){
                    adminUI ui = new adminUI(controller);
                    ui.start();
                }
                else try{
                    userUI ui = new userUI(controller, cmd);
                    ui.start();
                } catch(RepositoryException e){
                    System.out.println("Invalid username!");
                }
            }
        }
        System.out.println("Sayonara");
    }
}
