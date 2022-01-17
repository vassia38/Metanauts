package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReportController{
    Controller serviceController;
    User currentUser;
    @FXML TextField afterTextField;
    @FXML TextField beforeTextField;
    @FXML Button createButton;


    public void afterLoad(Controller controller, User user) {
        this.serviceController = controller;
        this.currentUser = user;
    }

    public void createEvent(ActionEvent actionEvent) {
        String after = this.afterTextField.getText();
        String before = this.beforeTextField.getText();
        LocalDate afterDate, beforeDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            afterDate = LocalDate.parse(after, formatter);
            beforeDate = LocalDate.parse(before, formatter);
        } catch (DateTimeParseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot create PDF");
            alert.setHeaderText("Invalid date!");
            alert.showAndWait();
            return;
        }
        String path = "D:\\FACULTATE\\Semestru 3\\MAP\\LAB\\metanauts\\";
        String filename = currentUser.getUsername()+"_"+LocalDate.now()+".pdf";
        this.serviceController.saveMessageReportToPDF(path,filename,afterDate,beforeDate,currentUser);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Message report created succesfully!");
        confirmation.setContentText(filename + " can be found at " + path);
        confirmation.getButtonTypes().remove(1);
        confirmation.show();
        Node source = (Node)actionEvent.getSource();
        Stage stage =(Stage) source.getScene().getWindow();
        stage.close();
    }
}
