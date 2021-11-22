package com.main;

import com.main.controller.Controller;
import com.main.controller.ControllerClass;
import com.main.repository.RepositoryException;
import com.main.repository.db.FriendshipDbRepository;
import com.main.repository.db.RequestDbRepository;
import com.main.repository.db.UserDbRepository;
import com.main.service.FriendshipService;
import com.main.service.RequestService;
import com.main.service.UserService;
import com.main.model.validators.PrietenieValidator;
import com.main.model.validators.UtilizatorValidator;
import com.main.view.adminUI;
import com.main.view.userUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";
        UtilizatorValidator userValidator = new UtilizatorValidator();

        PrietenieValidator friendshipValidator = new PrietenieValidator();
        UserDbRepository userRepo = new UserDbRepository(
                url, username,password, userValidator);
        FriendshipDbRepository friendshipRepo =
                new FriendshipDbRepository(url, username, password, friendshipValidator);
        RequestDbRepository requestRepo =
                new RequestDbRepository(url, username, password);
        UserService userService =
                new UserService(userRepo);
        FriendshipService friendshipService =
                new FriendshipService(friendshipRepo);
        RequestService requestService =
                new RequestService(requestRepo);
        Controller controller =
                new ControllerClass(userService,friendshipService,requestService);
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
