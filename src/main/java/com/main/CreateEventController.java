package com.main;

import com.main.controller.Controller;
import com.main.model.SocialEvent;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.events.GroupEvent;
import com.main.utils.observer.OperationType;
import com.main.utils.observer.UpdateType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CreateEventController {

    private User currentUser;
    private Controller serviceController;

    @FXML
    TextField nameTextField;
    @FXML
    TextField dateTextField;
    @FXML
    Button createButton;

    @FXML
    public void initialize() {
    }

    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void createEvent(ActionEvent actionEvent) {
        String eventName = this.nameTextField.getText();
        LocalDateTime eventDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            eventDate = LocalDateTime.parse(this.dateTextField.getText(), formatter);
        } catch (DateTimeParseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot create event");
            alert.setHeaderText("Invalid date!");
            alert.showAndWait();
            return;
        }
        if(eventName == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot create event");
            alert.setHeaderText("Event name is empty!");
            alert.showAndWait();
            return;
        }
        List<Long> ids = new ArrayList<>();
        ids.add(currentUser.getId());
        this.serviceController.createEvent(new SocialEvent(eventName,eventDate, ids));
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Event created succesfully!");
        confirmation.getButtonTypes().remove(1);
        confirmation.show();
        Node source = (Node)actionEvent.getSource();
        Stage stage =(Stage) source.getScene().getWindow();
        stage.close();
    }
}
