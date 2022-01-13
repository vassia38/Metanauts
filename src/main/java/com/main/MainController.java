package com.main;

import com.main.controller.Controller;
import com.main.model.Group;
import com.main.model.Request;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.observer.Observer;
import com.main.model.Friendship;
import com.main.repository.RepositoryException;
import com.main.utils.observer.OperationType;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class MainController implements Observer {

    private User currentUser;
    private Controller serviceController;
    private User shownUser;
    ObservableList<String> usernames = FXCollections.observableArrayList();
    ObservableList<Request> requests = FXCollections.observableArrayList();
    ObservableList<Request> solvedRequests = FXCollections.observableArrayList();
    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<Group> groups = FXCollections.observableArrayList();
    FilteredList<String> filteredItems = new FilteredList<>(usernames);
    Node requestsPage;
    Node eventsPage;
    RequestsController requestsController;
    EventsController eventsController;

    @FXML VBox document;
    @FXML StackPane body;
    @FXML VBox userPage;
    @FXML VBox friendliestBar;

    @FXML BorderPane menuBarShadow;
    @FXML VBox menuBar;


    @FXML Button homeButton;
    @FXML Button friendlistButton;
    @FXML Button requestsButton;
    @FXML Button eventsButton;

    @FXML ImageView menu;
    @FXML ImageView homeLogo;


    @FXML ComboBox<String> comboBoxSearch;
    @FXML Button searchButton;

    @FXML Button messageButton;
    @FXML Button addFriendButton;
    @FXML Button cancelRequestButton;
    @FXML Button removeFriendButton;
    @FXML Button createGroupButton;
    @FXML Button createEventButton;
    @FXML Label profileTitle;

    @FXML TableColumn<User, String> name_friend;
    @FXML TableView<User> tableViewFriends;

    @FXML TableView<Group> tableViewGroups;
    @FXML TableColumn<Group, String> groupName;


    private void animateSideBar(Node bar, Boolean visible) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), bar);
        double fromValue = 1.0, toValue = 0.0;
        if(visible) {
            fromValue = 0; toValue = 1.0;
        }
        transition.setFromValue(fromValue);
        transition.setToValue(toValue);
        transition.play();
        bar.setVisible(visible);
    }
    @FXML
    public void initialize() {
        body.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println(event.getTarget());
            if(event.getTarget() != menuBarShadow && friendliestBar.isVisible()) {
                this.animateSideBar(friendliestBar, !friendliestBar.isVisible());
            }
            if(comboBoxSearch.isFocused()) {
                document.requestFocus();
                event.consume();
            }
        });
        menuBarShadow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(menuBar.isVisible()) {
                this.animateSideBar(menuBar, !menuBar.isVisible());
                this.menuBarShadow.setVisible(menuBar.isVisible());
            }
        });
        menu.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.animateSideBar(menuBar, !menuBar.isVisible());
            this.menuBarShadow.setVisible(menuBar.isVisible());
            event.consume();
        });
        homeLogo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.showPage(event, userPage, currentUser);
            event.consume();
        });
        homeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.showPage(event, userPage, currentUser);
            event.consume();
        });
        requestsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                this.showPage(event, requestsPage, currentUser);
                event.consume();
        });
        friendlistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.animateSideBar(friendliestBar, !friendliestBar.isVisible());
            event.consume();
        });
        eventsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.showPage(event, eventsPage, currentUser);
            event.consume();
        });

        tableViewFriends.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User user = tableViewFriends.getSelectionModel().getSelectedItem();
                System.out.println("Double-clicked on " + user);
                this.showPage(event, userPage, user);
                event.consume();
            }
        });

        tableViewGroups.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Group group = tableViewGroups.getSelectionModel().getSelectedItem();
                System.out.println("Double-clicked on " + group);
                this.openGroupChat(group);
            }
        });

        this.menuBarShadow.setVisible(false);
        this.menuBarShadow.managedProperty().bind(this.menuBarShadow.visibleProperty());
        this.menuBar.setVisible(false);
        this.menuBar.managedProperty().bind(this.menuBar.visibleProperty());
        this.friendliestBar.setVisible(false);
        this.friendliestBar.managedProperty().bind(this.friendliestBar.visibleProperty());
        this.addFriendButton.setVisible(false);
        this.addFriendButton.managedProperty().bind(this.addFriendButton.visibleProperty());
        this.removeFriendButton.setVisible(false);
        this.removeFriendButton.managedProperty().bind(this.removeFriendButton.visibleProperty());
        this.cancelRequestButton.setVisible(false);
        this.cancelRequestButton.managedProperty().bind(this.cancelRequestButton.visibleProperty());
        this.messageButton.setVisible(false);
        this.messageButton.managedProperty().bind(this.removeFriendButton.visibleProperty());
        this.createGroupButton.managedProperty().bind(this.createGroupButton.visibleProperty());
        this.createEventButton.managedProperty().bind(this.createEventButton.visibleProperty());

        name_friend.setCellValueFactory( param ->
                new ReadOnlyStringWrapper ( param.getValue().getFirstName() + " " + param.getValue().getLastName()));
        tableViewFriends.setItems(this.friends);

        groupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableViewGroups.setItems(this.groups);

        comboBoxSearch.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBoxSearch.getEditor();
            final String selected = comboBoxSearch.getSelectionModel().getSelectedItem();

            // This needs run on the GUI thread to avoid the error described
            // here: https://bugs.openjdk.java.net/browse/JDK-8081700.
            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> item.toUpperCase().startsWith(newValue.toUpperCase()));
                }
            });
        });
        comboBoxSearch.setItems(filteredItems);
    }

    /***
     * load profile page of a certain user
     * @param user User
     */
    private void showProfileContent(User user) {
        this.profileTitle.textProperty().set(user.getFirstName() + " " +user.getLastName() +
                "\n" + user.getUsername());
        this.shownUser = user;
        if(!user.getUsername().equals(currentUser.getUsername())) {
            this.createGroupButton.setVisible(false);
            this.createEventButton.setVisible(false);
            //friends
            if(this.friends.contains(user)) {
                this.messageButton.setVisible(true);
                this.addFriendButton.setVisible(false);
                this.removeFriendButton.setVisible(true);
                this.cancelRequestButton.setVisible(false);
            }
            else {
                Request sentRequest = this.serviceController.findRequest(new Request(currentUser.getId(),shownUser.getId()));
                //request already sent by current user
                if(sentRequest != null) {
                    this.messageButton.setVisible(false);
                    this.addFriendButton.setVisible(false);
                    this.removeFriendButton.setVisible(false);
                    this.cancelRequestButton.setVisible(sentRequest.getStatus().equals("pending"));
                }
                //request already sent to current user
                else if(this.serviceController.findRequest(new Request(shownUser.getId(),currentUser.getId())) != null) {
                    this.messageButton.setVisible(false);
                    this.addFriendButton.setVisible(false);
                    this.removeFriendButton.setVisible(false);
                    this.cancelRequestButton.setVisible(false);
                }
                //no connection
                else {
                    this.messageButton.setVisible(false);
                    this.addFriendButton.setVisible(true);
                    this.removeFriendButton.setVisible(false);
                    this.cancelRequestButton.setVisible(false);
                }
            }
        }
        //same person
        else {
            this.messageButton.setVisible(false);
            this.removeFriendButton.setVisible(false);
            this.addFriendButton.setVisible(false);
            this.cancelRequestButton.setVisible(false);
            this.createGroupButton.setVisible(true);
            this.createEventButton.setVisible(true);
        }
    }

    private void showPage(MouseEvent event, Node root, User user) {
        this.body.getChildren().remove(0);
        this.body.getChildren().add(0, root);
        if(root == userPage)
            showProfileContent(user);
    }

    /***
     * custom initializer to be explicitly called after the fxml file has been loaded
     * @param serviceController Controller
     * @param user current user
     */
    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
        this.showProfileContent(user);
        this.serviceController.addObserver(this);

        this.updateFriends(null);
        this.updateUsers(null);
        this.updateGroups(null);
        FXMLLoader requestsLoader = new FXMLLoader(this.getClass().getResource("requests-view.fxml"));
        FXMLLoader eventsLoader = new FXMLLoader(this.getClass().getResource("event-view.fxml"));
        try{
            requestsPage = requestsLoader.load();
            requestsController = requestsLoader.getController();
            requestsController.afterLoad(this.serviceController, this.currentUser);
            eventsPage = eventsLoader.load();
            eventsController = eventsLoader.getController();
            eventsController.afterLoad(this.serviceController, this.currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateUsernames() {
        this.usernames.clear();
        Iterable<User> users = this.serviceController.getAllUsers();
        this.setUsernames(users);
    }

    // FRIENDS OBSERVER METHODS
    private User findUserByFriendship(Friendship friendship) {
        if(friendship == null)
            return null;
        Long idUser = friendship.getId().getLeft().equals(currentUser.getId())
                ? friendship.getId().getRight()
                : friendship.getId().getLeft();
        return this.serviceController.findUserById(idUser);
    }
    public void addFriendObserverMethod(Friendship friendship) {
        User newFriend = this.findUserByFriendship(friendship);
        System.out.println(newFriend);
        if(newFriend != null){
            this.friends.add(newFriend);
        }
    }
    public void deleteFriendObserverMethod(Friendship friendship) {
        User newStranger = this.findUserByFriendship(friendship);
        if(newStranger != null) {
            this.friends.remove(newStranger);
        }
    }
    public void updateFriendObserverMethod(Friendship friendship) {
        User updatedFriend = this.findUserByFriendship(friendship);
        if(updatedFriend != null) {
            for(User u : this.friends) {
                if(u.getId().equals(updatedFriend.getId())) {
                    this.friends.set(this.friends.indexOf(u), updatedFriend);
                    return;
                }
            }
        }
    }
    public final Map<OperationType, Method> mapFriendsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, MainController.class.getMethod("addFriendObserverMethod", Friendship.class));
            put(OperationType.DELETE, MainController.class.getMethod("deleteFriendObserverMethod", Friendship.class));
            put(OperationType.UPDATE, MainController.class.getMethod("updateFriendObserverMethod", Friendship.class));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateFriends(Event event) {
        if(event == null) {
            System.out.println("load all data for friends list");
            this.friends.clear();
            List<User> friends = this.serviceController.getAllFriends(currentUser);
            this.friends.addAll(friends);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            System.out.println(event.getObject());
            this.mapFriendsOperations.get(operationType).invoke(this, event.getObject());
            System.out.println(operationType.toString() + " Friend executed successfully");
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    //REQUESTS OBSERVER METHODS
    public void addRequestObserverMethod(Request request) {
        this.requests.add(request);
    }
    public void deleteRequestObserverMethod(Request request) {
        this.requests.remove(request);
    }
    public final Map<OperationType, Method> mapRequestsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, MainController.class.getMethod("addRequestObserverMethod", Request.class));
            put(OperationType.DELETE, MainController.class.getMethod("deleteRequestObserverMethod", Request.class));
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateRequests(Event event) {
        if(event == null) {
            System.out.println("load all data for requests table");
            this.requests.clear();
            Iterable<Request> requests = this.serviceController.getAllRequests(this.currentUser);
            this.setRequests(requests);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            System.out.println(operationType.toString() + " Request executed successfully");
            this.mapRequestsOperations.get(operationType).invoke(this, event.getObject());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SOLVED REQUESTS OBSERVER METHODS
    public void addSolvedRequestObserverMethod(Request request) {
        this.solvedRequests.add(request);
    }
    public final Map<OperationType, Method> mapSolvedRequestsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, MainController.class.getMethod("addSolvedRequestObserverMethod", Request.class));
            put(OperationType.DELETE, null);
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateSolvedRequests(Event event) {
        if(event == null) {
            System.out.println("load all data for solved requests table");
            this.solvedRequests.clear();
            Iterable<Request> requests = this.serviceController.getAllAnsweredRequests(this.currentUser);
            this.setSolvedRequests(requests);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            mapSolvedRequestsOperations.get(operationType).invoke(this, event.getObject());
            System.out.println(operationType.toString() + " Solved request executed successfully");
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    //GROUPS OBSERVER METHODS
    public void addGroupObserverMethod(Group gr) {
        this.groups.add(gr);
    }
    public final Map<OperationType, Method> mapGroupsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, MainController.class.getMethod("addGroupObserverMethod", Group.class));
            put(OperationType.DELETE, null);
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateGroups(Event event) {
        if(event == null) {
            System.out.println("load all data for requests table");
            this.groups.clear();
            Iterable<Group> groups = this.serviceController.getAllGroups(this.currentUser);
            this.setGroups(groups);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            mapGroupsOperations.get(operationType).invoke(this, event.getObject());
            System.out.println(operationType.toString() + " Group executed succesfully");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void searchUser() {
        System.out.println("Searching for " + this.comboBoxSearch.getEditor().getText());
        try {
            User user = this.serviceController.findUserByUsername(this.comboBoxSearch.getEditor().getText());
            this.showProfileContent(user);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    private void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void setUsernames(Iterable<User> users) {
        users.forEach( u -> this.usernames.add(u.getUsername()));
    }

    public void addFriend() {
        try {
            Request request = new Request(currentUser.getId(), shownUser.getId());
            this.serviceController.addRequest(request);
            this.showProfileContent(shownUser);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }
    public void removeFriend() {
        try {
            Friendship friendship = new Friendship(currentUser.getId(), shownUser.getId());
            this.serviceController.deleteFriendship(friendship);
            Request request1 = this.serviceController.findRequest(new Request(currentUser.getId(), shownUser.getId()));
            if(request1 != null) {
                this.serviceController.deleteRequest(request1);
            }
            else {
                Request request2 = this.serviceController.findRequest(new Request(shownUser.getId(), currentUser.getId()));
                if(request2 != null) {
                    this.serviceController.deleteRequest(request2);
                }
            }
            this.showProfileContent(shownUser);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void createGroup(){
        try {
            System.out.println("Opening create group window" + currentUser);
            Stage groupStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("create-group-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 680, 800);
            groupStage.setTitle("Metanauts - " + currentUser.getUsername() + " | "
                    + "create new group");
            groupStage.setScene(scene);
            try{
                groupStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
            } catch(NullPointerException e){
                System.out.println("icon could not load!");
            }
            groupStage.show();
            CreateGroupController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, currentUser);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Can't create a group!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGroups(Iterable<Group> groups) {
        groups.forEach(gr -> this.groups.add(gr));
    }

    private void setRequests(Iterable<Request> requests) {
        requests.forEach( req -> this.requests.add(req));
    }

    private void setSolvedRequests(Iterable<Request> requests) {
        requests.forEach( req -> this.solvedRequests.add(req));
    }

    public void cancelRequest() {
        try {
            Request request = new Request(currentUser.getId(),shownUser.getId());
            this.serviceController.deleteRequest(request);
            this.showProfileContent(shownUser);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void openGroupChat(Group group) {
        if(group == null)
            return;
        try {
            System.out.println("Opening group chat window" + currentUser);
            Stage groupChatStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("group-chat-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 680, 800);
            groupChatStage.setTitle("Metanauts - " + currentUser.getUsername() + " | "
                    + group.getName());
            groupChatStage.setScene(scene);
            try{
                groupChatStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
            } catch(NullPointerException e){
                System.out.println("icon could not load!");
            }
            GroupChatController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, currentUser, group);
            groupChatStage.show();
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Can't open group chat!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void messageFriend() {
        try {
            System.out.println("Opening chat with: " + shownUser);
            Stage chatStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("chat-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 680, 800);
            chatStage.setTitle("Metanauts - " + currentUser.getUsername() + " | "
                    + shownUser.getUsername());
            chatStage.setScene(scene);
            try{
                chatStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
            } catch(NullPointerException e){
                System.out.println("icon could not load!");
            }
            ChatController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, currentUser, shownUser);
            chatStage.show();
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Can't chat with this person!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUsers(Event event) {
        this.updateUsernames();
    }
    @Override
    public void updateMessages(Event event) {
        // nothing
    }
    @Override
    public void updateGroupMessages(Event event) {

    }

    public void createEvent(ActionEvent actionEvent) {
        //TODO
    }
}
