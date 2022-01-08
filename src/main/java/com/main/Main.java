package com.main;

import com.main.controller.Controller;
import com.main.controller.ControllerClass;
import com.main.model.validators.FriendshipValidator;
import com.main.model.validators.MessageValidator;
import com.main.model.validators.UserValidator;
import com.main.repository.db.*;
import com.main.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";

        UserValidator userValidator = new UserValidator();
        UserDbRepository userRepo = new UserDbRepository(url, username,password, userValidator);

        FriendshipValidator friendshipValidator = new FriendshipValidator();
        FriendshipDbRepository friendshipRepo = new FriendshipDbRepository(url, username, password, friendshipValidator);

        MessageValidator messageValidator = new MessageValidator();
        MessageDbRepository messageRepo = new MessageDbRepository(url,username,password, messageValidator);

        GroupDbRepository groupRepo = new GroupDbRepository(url, username, password);
        /*for(Group gr : groupRepo.findAll(3L)) {
            System.out.println(gr);
        }*/

        RequestDbRepository requestRepo =
                new RequestDbRepository(url, username, password);

        UserService userService = new UserService(userRepo);
        FriendshipService friendshipService = new FriendshipService(friendshipRepo);
        MessageService messageService = new MessageService(messageRepo);
        GroupService groupService = new GroupService(groupRepo);
        RequestService requestService = new RequestService(requestRepo);
        Controller controller = new ControllerClass(userService,friendshipService,
                messageService, requestService, groupService);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Parent root = fxmlLoader.load();
        LoginController mainController = fxmlLoader.getController();
        mainController.setServiceController(controller);
        mainController.afterLoad();

        Scene scene = new Scene(root, 695, 427);
        primaryStage.setTitle("Metanauts - login");
        primaryStage.setScene(scene);
        try{
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        } catch(NullPointerException e){
            System.out.println("icon could not load!");
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}