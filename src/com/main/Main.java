package com.main;

import com.main.controller.Controller;
import com.main.controller.ControllerClass;
import com.main.repository.db.FriendshipDbRepository;
import com.main.repository.db.UserDbRepository;
import com.main.service.FriendshipService;
import com.main.service.UserService;
import com.main.model.validators.PrietenieValidator;
import com.main.model.validators.UtilizatorValidator;
import com.main.view.adminUI;

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
        UserService userService =
                new UserService(userRepo);
        FriendshipService friendshipService =
                new FriendshipService(friendshipRepo);
        Controller controller =
                new ControllerClass(userService,friendshipService);
        String user = "";
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
                user = keyboard.nextLine();
                if(user.equals("admin")){
                    adminUI adminUi = new adminUI(controller);
                    adminUi.start();
                }
                else if(controller.findUserByUsername(user) != null){

                }
                else{
                    System.out.println("Invalid username!");
                }
            }
        }
        System.out.println("Sayonara");
    }
}
