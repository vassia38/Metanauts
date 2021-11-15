package com.main;

import com.main.controller.Controller;
import com.main.controller.ControllerClass;
import com.main.repository.db.FriendshipDbRepository;
import com.main.repository.db.UserDbRepository;
import com.main.service.FriendshipService;
import com.main.service.UserService;
import com.main.model.Friendship;
import com.main.model.Tuple;
import com.main.model.User;
import com.main.model.validators.PrietenieValidator;
import com.main.model.validators.UtilizatorValidator;
import com.main.repository.Repository;
import com.main.view.UI;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";
        UtilizatorValidator userValidator = new UtilizatorValidator();

        PrietenieValidator friendshipValidator = new PrietenieValidator();
        Repository<Long, User> userRepo = new UserDbRepository(
                url, username,password, userValidator);
        Repository<Tuple<Long,Long>, Friendship> friendshipRepo =
                new FriendshipDbRepository(url, username, password, friendshipValidator);
        UserService userService =
                new UserService(userRepo);
        FriendshipService friendshipService =
                new FriendshipService(friendshipRepo);
        Controller controller =
                new ControllerClass(userService,friendshipService);

        UI ui = new UI(controller);
        ui.start();
        System.out.println("Sayonara");
    }
}
