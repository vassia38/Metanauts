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
    TableColumn<User, String> friendUser;
    @FXML
    TableColumn<User, String> first_name;
    @FXML
    TableColumn<User, String> last_name;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    Button searchButton;

    @FXML
    public void initialize() {
        friendUser.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        first_name.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableViewFriends.setItems(friends);

        comboBoxSearch.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBoxSearch.getEditor();
            final String selected = comboBoxSearch.getSelectionModel().getSelectedItem();

            // This needs run on the GUI thread to avoid the error described
            // here: https://bugs.openjdk.java.net/browse/JDK-8081700.
            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> {
                        if (item.toUpperCase().startsWith(newValue.toUpperCase())) {
                            return true;
                        } else {
                            return false;
                        }
                    });
                }
            });
        });

        comboBoxSearch.setItems(filteredItems);
    }

    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);

        this.friends.removeAll();
        List<User> friends = serviceController.getAllFriends(currentUser);
        this.friends.addAll(friends);

        this.usernames.removeAll();
        Iterable<User> users = this.serviceController.getAllUsers();
        this.setUsernames(users);
    }

    public void searchUser(ActionEvent actionEvent) {

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
