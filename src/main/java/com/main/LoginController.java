package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import com.main.repository.RepositoryException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    private Controller serviceController;
    ObservableList<User> users = FXCollections.observableArrayList();

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
        TextField locvar = loginTextField;
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<User>() {
                    @Override
                    public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                        System.out.println("User " + newValue + " was selected");
                        locvar.textProperty().set(newValue.getUsername());
                    }
                }
        );
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
            System.out.println("Logging in as: " + user);
            Node source = (Node) event.getSource();
            Stage current = (Stage) source.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 1024, 768);
            current.setTitle("Metanauts - " + user.getUsername());
            current.setScene(scene);
            MainController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, user);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("This user doesn't exist!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}