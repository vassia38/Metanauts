package com.main;

import com.main.controller.Controller;
import com.main.model.Request;
import com.main.model.User;
import com.main.utils.observer.Observer;
import com.main.model.Friendship;
import com.main.repository.RepositoryException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainController implements Observer {
    private User currentUser;
    private Controller serviceController;
    ObservableList<String> usernames = FXCollections.observableArrayList();
    ObservableList<Request> requests = FXCollections.observableArrayList();
    ObservableList<User> friends = FXCollections.observableArrayList();
    FilteredList<String> filteredItems = new FilteredList<>(usernames);
    private User shownUser;

    @FXML
    ImageView homeLogo;

    @FXML
    ComboBox<String> comboBoxSearch;
    @FXML
    Button searchButton;

    @FXML
    Button messageButton;

    /***
     * TODO functionalitatea F1 pt butoanele astea 2
     */
    @FXML
    Button addFriendButton;
    @FXML
    Button cancelRequestButton;
    @FXML
    Button removeFriendButton;
    @FXML
    Label profileTitle;
    @FXML
    TableView<Request> tableViewRequests;
    @FXML
    TableColumn<Request, String> fromUser;
    @FXML
    TableColumn<Request, Request> acceptFriendship;
    @FXML
    TableColumn<Request, Request> rejectFriendship;

    @FXML
    TableColumn<User, String> friendUser;
    @FXML
    TableColumn<User, String> first_name;
    @FXML
    TableColumn<User, String> last_name;
    @FXML
    TableView<User> tableViewFriends;


    @FXML
    public void initialize() {
        homeLogo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            showProfile(currentUser);
            event.consume();
        });

        tableViewFriends.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                User user = tableViewFriends.getSelectionModel().getSelectedItem();
                System.out.println("Double-clicked on " + user);
                this.showProfile(user);
            }
        });
        this.addFriendButton.setVisible(false);
        this.removeFriendButton.setVisible(false);
        this.cancelRequestButton.setVisible(false);
        this.messageButton.setVisible(false);
        this.addFriendButton.managedProperty().bind(this.addFriendButton.visibleProperty());
        this.removeFriendButton.managedProperty().bind(this.removeFriendButton.visibleProperty());
        this.messageButton.managedProperty().bind(this.removeFriendButton.visibleProperty());
        this.tableViewRequests.managedProperty().bind(this.tableViewRequests.visibleProperty());

        friendUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewFriends.setItems(friends);

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

        fromUser.setCellValueFactory(param -> {
            User user = this.serviceController.findUserById(param.getValue().getId().getLeft());
            return new ReadOnlyObjectWrapper<>(user.getUsername());
        });

        acceptFriendship.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        acceptFriendship.setCellFactory(
            param -> new TableCell<>() {
                private final Button addButton = new Button("Accept");

                @Override
                protected void updateItem(Request req, boolean empty) {
                    super.updateItem(req, empty);
                    if (req == null) {
                        setGraphic(null);
                        return;
                    }
                    setGraphic(addButton);
                    addButton.setOnAction(
                            event -> {
                                serviceController.answerRequest(req,"approve");
                                /*updateFriends();*/
                                getTableView().getItems().remove(req);
                            }
                    );
                }
            });

        rejectFriendship.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        rejectFriendship.setCellFactory(
            param -> new TableCell<>() {
                private final Button deleteButton = new Button("Reject");

                @Override
                protected void updateItem(Request req, boolean empty) {
                    super.updateItem(req, empty);
                    if (req == null) {
                        setGraphic(null);
                        return;
                    }
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(
                            event -> {
                                serviceController.answerRequest(req,"reject");
                                getTableView().getItems().remove(req);
                            }
                    );
                }
            });

        tableViewRequests.setItems(this.requests);
    }

    /***
     * load profile page of a certain user
     * @param user User
     */
    private void showProfile(User user) {
        this.profileTitle.textProperty().set(user.getFirstName() + " " +user.getLastName() +
                "\n" + user.getUsername());
        this.shownUser = user;
        if(!user.getUsername().equals(currentUser.getUsername())) {
            this.tableViewRequests.setVisible(false);
            //friends
            if(this.friends.contains(user)) {
                this.messageButton.setVisible(true);
                this.addFriendButton.setVisible(false);
                this.removeFriendButton.setVisible(true);
                this.cancelRequestButton.setVisible(false);
            }
            //request already sent by current user
            else if(this.serviceController.findRequest(new Request(currentUser.getId(),shownUser.getId())) != null) {
                this.messageButton.setVisible(false);
                this.addFriendButton.setVisible(false);
                this.removeFriendButton.setVisible(false);
                this.cancelRequestButton.setVisible(true);
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
        //same person
        else {
            this.messageButton.setVisible(false);
            this.removeFriendButton.setVisible(false);
            this.addFriendButton.setVisible(false);
            this.cancelRequestButton.setVisible(false);
            this.tableViewRequests.setVisible(true);
        }
    }


    /***
     * custom initializer to be explicitly called after the fxml file has been loaded
     * @param serviceController Controller
     * @param user current user
     */
    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
        this.showProfile(user);
        this.serviceController.addObserver(this);

        this.updateFriends();

        this.updateUsernames();

        this.updateRequests();
    }



    void updateUsernames() {
        this.usernames.clear();
        Iterable<User> users = this.serviceController.getAllUsers();
        this.setUsernames(users);
    }
    @Override
    public void updateFriends() {
        this.friends.clear();
        List<User> friends = this.serviceController.getAllFriends(currentUser);
        this.friends.addAll(friends);
    }

    @Override
    public void updateRequests() {
        this.requests.clear();
        Iterable<Request> requests = this.serviceController.showRequests(this.currentUser);
        this.setRequests(requests);
    }

    @Override
    public void updateUsers() {
        this.updateUsernames();
    }

    @Override
    public void updateMessages() {
        // nothing
    }



    public void searchUser() {
        System.out.println("Searching for " + this.comboBoxSearch.getEditor().getText());
        try {
            User user = this.serviceController.findUserByUsername(this.comboBoxSearch.getEditor().getText());
            this.showProfile(user);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setUsernames(Iterable<User> users) {
        users.forEach( u -> this.usernames.add(u.getUsername()));
    }
    public void addFriend() {
        try {
            Request request = new Request(currentUser.getId(), shownUser.getId());
            this.serviceController.addRequest(request);
            this.showProfile(shownUser);
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
            this.showProfile(shownUser);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void setRequests(Iterable<Request> requests) {
        requests.forEach( req -> this.requests.add(req));
    }

    public void cancelRequest() {
        try {
            Request request = new Request(currentUser.getId(),shownUser.getId());
            this.serviceController.deleteRequest(request);
            this.showProfile(shownUser);
        } catch (RepositoryException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void messageFriend(ActionEvent actionEvent) {
        try {
            System.out.println("Opening chat with: " + shownUser);
            Stage chatStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("chat-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 680, 920);
            chatStage.setTitle("Metanauts - " + currentUser.getUsername() + " | "
                    + shownUser.getUsername());
            chatStage.setScene(scene);
            try{
                chatStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
            } catch(NullPointerException e){
                System.out.println("icon could not load!");
            }
            chatStage.show();
            ChatController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, currentUser, shownUser);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Can't chat with this person!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
