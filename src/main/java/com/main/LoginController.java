package com.main;

import com.main.controller.Controller;
import com.main.model.Tuple;
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

public class LoginController{
    private Controller serviceController;

    @FXML TextField usernameTextField;
    @FXML TextField firstnameTextField;
    @FXML TextField lastnameTextField;
    @FXML TextField passwordTextField;

    @FXML Button login_button;
    @FXML Button register_button;

    @FXML Label questionLabel;
    @FXML Button registerViewButton;
    @FXML Button loginViewButton;


    @FXML
    public void initialize() {
        this.firstnameTextField.setVisible(false);
        this.firstnameTextField.managedProperty().bind(this.firstnameTextField.visibleProperty());
        this.lastnameTextField.setVisible(false);
        this.lastnameTextField.managedProperty().bind(this.lastnameTextField.visibleProperty());

        this.login_button.managedProperty().bind(this.login_button.visibleProperty());
        this.register_button.setVisible(false);
        this.register_button.managedProperty().bind(this.register_button.visibleProperty());

        this.registerViewButton.managedProperty().bind(this.registerViewButton.visibleProperty());
        this.loginViewButton.setVisible(false);
        this.loginViewButton.managedProperty().bind(this.loginViewButton.visibleProperty());
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }


    private User login(String username, String userPassword) {
        String msg = "";
        try {
            User user = this.serviceController.findUserByUsername(username);
            String hashcodedPassword = this.serviceController.hashCodePassword(username, userPassword);
            if(!hashcodedPassword.equals(user.getUserPassword())) {
                msg = "Incorrect password!\n";
            }
            else {
                return user;
            }
        } catch (RepositoryException ex) {
            msg = "This user doesn't exist!\n";
        }
        if(!msg.equals("")){
            throw new RepositoryException(msg);
        }
        return null;
    }


    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if(username.equals("")){
            return;
        }
        try {
            User user = login(username, password);
            System.out.println("Logging in as: " + user);
            Node source = (Node) event.getSource();
            Stage current = (Stage) source.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            MainController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, user);
            Scene scene = new Scene(root, 1024, 768);
            current.setTitle("Metanauts - " + user.getUsername());
            current.setScene(scene);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User register(String username, String userPassword, String firstName, String lastName) {
        User user;
        String msg;
        try {
            this.serviceController.findUserByUsername(username);
            msg = "This user already exists!\nTry a different username.\n";
        } catch (RepositoryException ex) {
            Tuple<String, String> pair = this.serviceController.generatePassword(userPassword);
            String hashcodedPassword = pair.getLeft();
            String salt = pair.getRight();
            user = new User(username,firstName,lastName,hashcodedPassword);
            try{
                this.serviceController.addUser(user,salt);
                user = this.serviceController.findUserByUsername(username);
                return user;
            } catch (RepositoryException e) {
                msg = "Problem with adding user.\n Please try again.\n";
            }
        }
        if(!msg.equals(""))
            throw new RepositoryException(msg);
        return null;
    }

    @FXML
    public void onRegisterButtonClick(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String firstName = firstnameTextField.getText();
        String lastName = lastnameTextField.getText();
        if(username.equals("")){
            return;
        }
        if(password.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Password field can't be empty!\n");
            alert.showAndWait();
            return;
        }
        try {
            User user = register(username, password, firstName, lastName);
            System.out.println("Logging in as: " + user);
            Node source = (Node) event.getSource();
            Stage current = (Stage) source.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            MainController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, user);
            Scene scene = new Scene(root, 1024, 768);
            current.setTitle("Metanauts - " + user.getUsername());
            current.setScene(scene);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchRegister() {
        this.firstnameTextField.setVisible(true);
        this.lastnameTextField.setVisible(true);
        this.login_button.setVisible(false);
        this.register_button.setVisible(true);

        this.questionLabel.setText("Already have an account?");
        this.registerViewButton.setVisible(false);
        this.loginViewButton.setVisible(true);
    }

    public void switchLogin() {
        this.firstnameTextField.setVisible(false);
        this.lastnameTextField.setVisible(false);
        this.login_button.setVisible(true);
        this.register_button.setVisible(false);

        this.questionLabel.setText("No account?");
        this.registerViewButton.setVisible(true);
        this.loginViewButton.setVisible(false);
    }
}