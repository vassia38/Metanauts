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

    private void login(String username, String userPassword) {
        String errormsg = "";
        try {
            User user = this.serviceController.findUserByUsername(username);
            String hashcodedPassword = this.serviceController.hashCodePassword(username, userPassword);
            if(!hashcodedPassword.equals(user.getUserPassword())) {
                errormsg = "Incorrect password!\n";
            }
        } catch (RepositoryException ex) {
            errormsg = "This user doesn't exist!\n";
        }
        if(!errormsg.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(errormsg);
            alert.showAndWait();
        }
    }


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

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameTextField.getText();
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
            MainController ctrl = fxmlLoader.getController();
            ctrl.afterLoad(this.serviceController, user);
            Scene scene = new Scene(root, 1024, 768);
            current.setTitle("Metanauts - " + user.getUsername());
            current.setScene(scene);
        } catch(RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("This user doesn't exist!\n");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRegisterButtonClick(ActionEvent actionEvent) {
        //TODO
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