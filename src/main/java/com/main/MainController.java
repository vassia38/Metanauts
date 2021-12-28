package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainController {



    private User currentUser;
    private Controller serviceController;
    ObservableList<String> usernames = FXCollections.observableArrayList();
    ObservableList<User> friends = FXCollections.observableArrayList();
    FilteredList<String> filteredItems = new FilteredList<>(usernames);

    @FXML
    ComboBox<String> comboBoxSearch;
    @FXML
    Button searchButton;

    @FXML
    Button messageButton;
    @FXML
    Button addFriendButton;
    @FXML
    Button removeFriendButton;
    @FXML
    Label profileTitle;

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
        tableViewFriends.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                User user = tableViewFriends.getSelectionModel().getSelectedItem();
                System.out.println("Double-clicked on " + user);
                this.showProfile(user);
            }
        });
        this.addFriendButton.setVisible(false);
        this.removeFriendButton.setVisible(false);
        this.messageButton.setVisible(false);
        this.addFriendButton.managedProperty().bind(this.addFriendButton.visibleProperty());
        this.removeFriendButton.managedProperty().bind(this.removeFriendButton.visibleProperty());
        this.messageButton.managedProperty().bind(this.removeFriendButton.visibleProperty());

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
    }

    private void showProfile(User user) {
        this.profileTitle.textProperty().set(user.getFirstName() + " " +user.getLastName() +
                "\n" + user.getUsername());
        if(user != currentUser) {
            if(this.friends.contains(user)){
                this.messageButton.setVisible(true);
                this.removeFriendButton.setVisible(true);
            }
            else {
                this.addFriendButton.setVisible(true);
            }
        }
    }

    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
        this.showProfile(user);

        this.friends.removeAll();
        List<User> friends = serviceController.getAllFriends(currentUser);
        this.friends.addAll(friends);

        this.usernames.removeAll();
        Iterable<User> users = this.serviceController.getAllUsers();
        this.setUsernames(users);
    }

    public void searchUser(ActionEvent actionEvent) {
        System.out.println("Searching for " + this.comboBoxSearch.getEditor().getText());
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public void setUsernames(Iterable<User> users){
        users.forEach( u -> this.usernames.add(u.getUsername()));
    }


}
