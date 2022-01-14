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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        RequestDbRepository requestRepo =
                new RequestDbRepository(url, username, password);

        SocialEventDbRepository eventRepo = new SocialEventDbRepository(url, username, password);

        UserService userService = new UserService(userRepo);
        FriendshipService friendshipService = new FriendshipService(friendshipRepo);
        MessageService messageService = new MessageService(messageRepo);
        GroupService groupService = new GroupService(groupRepo);
        RequestService requestService = new RequestService(requestRepo);
        SocialEventService eventService = new SocialEventService(eventRepo);
        Controller controller = new ControllerClass(userService,friendshipService,
                messageService, requestService, groupService, eventService);

        /*User user = controller.findUserByUsername("vassco");
        Iterable<GroupMessage> msgs = controller.getGroupConversation(user, "grupa 224");
        msgs.forEach(System.out::println);*/
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        controller.saveMessageReportToPDF(
                "D:\\FACULTATE\\Semestru 3\\MAP\\LAB\\metanauts\\",
                "rapport1.pdf",
                LocalDate.parse("2021-09-10 13:00", formatter),
                LocalDate.parse("2022-09-10 13:00", formatter),
                userService.findOneByUsername("vassco"));*/
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Parent root = fxmlLoader.load();
        LoginController mainController = fxmlLoader.getController();
        mainController.setServiceController(controller);

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