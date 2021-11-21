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
import com.main.service.MessageService;
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
        UserDbRepository userRepo = new UserDbRepository(url, username,password, userValidator);

        FriendshipValidator friendshipValidator = new FriendshipValidator();
        FriendshipDbRepository friendshipRepo = new FriendshipDbRepository(url, username, password, friendshipValidator);

        MessageValidator messageValidator = new MessageValidator();
        MessageDbRepository messageRepo = new MessageDbRepository(url,username,password, messageValidator);

        UserService userService = new UserService(userRepo);
        FriendshipService friendshipService = new FriendshipService(friendshipRepo);
        MessageService messageService = new MessageService(messageRepo);

        Controller controller = new ControllerClass(userService,friendshipService, messageService);

        List<User> all = new ArrayList<>();
        for(User u : controller.getAllUsers())
            all.add(u);
        /*controller.sendMessage(controller.findUserById(3L), all,
                "Now i'm spamming all",
                LocalDateTime.now(), 7L);*/
        for(Message m : controller.getAllMesagesOfUser("vassco")){
            System.out.println(m);
        }
        Scanner keyboard = new Scanner(System.in);
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
