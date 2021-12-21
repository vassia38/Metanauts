package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import com.main.repository.RepositoryException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    private Controller serviceController;
    ObservableList<User> users = FXCollections.observableArrayList();
    User currentUser = null;

    @FXML
    TableColumn<User, String> username;
    @FXML
    TableColumn<User, String> first_name;
    @FXML
    TableColumn<User, String> last_name;

    @FXML
    TableView<User> tableViewUsers;

    @FXML
    TextField loginTextField;

    @FXML
    Button login_button;


    @FXML
    public void initialize() {
        username.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        first_name.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableViewUsers.setItems(users);
    }

    public void afterLoad() {
        users.removeAll();
        List<User> list = this.getUsersList();
        users.addAll(list);
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    private List<User> getUsersList() {
        Iterable<User> users = serviceController.getAllUsers();
        List<User> userList = new ArrayList<>();
        for( User u : users){
            userList.add(u);
        }
        userList.forEach(System.out::println);
        return userList;
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = loginTextField.getText();
        if(username.equals("")){
            return;
        }
        try {
            User user = serviceController.findUserByUsername(username);
            currentUser = user;
            System.out.println(user);
        }catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("This user doesn't exist!\n");
            alert.showAndWait();
        }
    }
}