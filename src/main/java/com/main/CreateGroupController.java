package com.main;

import com.main.controller.Controller;
import com.main.model.Group;
import com.main.model.User;
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

import java.util.ArrayList;
import java.util.List;

public class CreateGroupController {

    private User currentUser;
    private Controller serviceController;
    ObservableList<User> friends = FXCollections.observableArrayList();

    @FXML
    TableColumn<User, String> friendUser;
    @FXML
    TableColumn<User, String> first_name;
    @FXML
    TableColumn<User, String> last_name;
    @FXML
    TableView<User> tableViewFriends;

    @FXML
    TextArea nameTextArea;
    @FXML
    Button createButton;

    @FXML
    public void initialize() {
        friendUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewFriends.setItems(this.friends);

        tableViewFriends.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
        this.friends.addAll(this.serviceController.getAllFriends(this.currentUser));
    }

    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void createGroup(ActionEvent actionEvent) {
        String groupName = this.nameTextArea.getText();
        if(groupName == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot create group");
            alert.setHeaderText("Group name is empty!");
            alert.showAndWait();
            return;
        }
        if(tableViewFriends.getSelectionModel().getSelectedItems() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot create group");
            alert.setHeaderText("No person selected!");
            alert.showAndWait();
            return;
        }
        List<User> members = new ArrayList<>(tableViewFriends.getSelectionModel().getSelectedItems());
        members.add(currentUser);
        this.serviceController.createGroup(groupName, members);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Group created succesfully!");
        confirmation.getButtonTypes().remove(1);
        confirmation.show();
        Node source = (Node)actionEvent.getSource();
        Stage stage =(Stage) source.getScene().getWindow();
        stage.close();
    }
}
