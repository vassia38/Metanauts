package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainController {


    private User currentUser;
    private Controller serviceController;
    ObservableList<User> users = FXCollections.observableArrayList();
    ObservableList<User> friends = FXCollections.observableArrayList();

    @FXML
    ComboBox comboBoxSearch;
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
        friendUser.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        first_name.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableViewFriends.setItems(friends);
    }

    public void afterLoad() {
        comboBoxSearch.setItems(users);
        this.friends.removeAll();
        List<User> friends = serviceController.getAllFriends(currentUser);
        this.friends.addAll(friends);
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public void setUsers(ObservableList<User> users){
        this.users = users;
    }

}
