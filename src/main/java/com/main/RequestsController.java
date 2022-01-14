package com.main;

import com.main.controller.Controller;
import com.main.model.Request;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.observer.Observer;
import com.main.utils.observer.OperationType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestsController implements Observer {
    private User currentUser;
    private Controller serviceController;
    ObservableList<Request> requests = FXCollections.observableArrayList();
    ObservableList<Request> solvedRequests = FXCollections.observableArrayList();

    @FXML
    TableView<Request> tableViewRequests;
    @FXML
    TableColumn<Request, String> fromUser;
    @FXML
    TableColumn<Request, Request> acceptFriendship;
    @FXML
    TableColumn<Request, Request> rejectFriendship;

    @FXML
    TableView<Request> historyTableViewRequests;
    @FXML
    TableColumn<Request, String> historyFromUser;
    @FXML
    TableColumn<Request, String> status;
    @FXML
    TableColumn<Request, String> dateSent;

    @FXML
    public void initialize() {
        fromUser.setCellValueFactory(param -> {
            User user = this.serviceController.findUserById(param.getValue().getId().getLeft());
            return new ReadOnlyObjectWrapper<>(user.getUsername());
        });

        acceptFriendship.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        acceptFriendship.setCellFactory(
                param -> new TableCell<>() {
                    private final Button addButton = new Button("Accept");

                    @Override
                    protected void updateItem(Request req, boolean empty) {
                        super.updateItem(req, empty);
                        if (req == null) {
                            setGraphic(null);
                            return;
                        }
                        setGraphic(addButton);
                        addButton.setOnAction(
                                event -> {
                                    serviceController.answerRequest(req,"approve");
                                    /*updateFriends();*/
                                    getTableView().getItems().remove(req);
                                }
                        );
                    }
                });

        rejectFriendship.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        rejectFriendship.setCellFactory(
                param -> new TableCell<>() {
                    private final Button deleteButton = new Button("Reject");

                    @Override
                    protected void updateItem(Request req, boolean empty) {
                        super.updateItem(req, empty);
                        if (req == null) {
                            setGraphic(null);
                            return;
                        }
                        setGraphic(deleteButton);
                        deleteButton.setOnAction(
                                event -> {
                                    serviceController.answerRequest(req,"reject");
                                    getTableView().getItems().remove(req);
                                }
                        );
                    }
                });

        tableViewRequests.setItems(this.requests);

        historyFromUser.setCellValueFactory(param -> {
            Long id = param.getValue().getId().getLeft().equals(currentUser.getId())
                    ? param.getValue().getId().getRight()
                    : param.getValue().getId().getLeft();
            User user = this.serviceController.findUserById(id);
            return new ReadOnlyObjectWrapper<>(user.getUsername());
        });
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateSent.setCellValueFactory(new PropertyValueFactory<>("date"));
        historyTableViewRequests.setItems(this.solvedRequests);
    }

    public void afterLoad(Controller serviceController, User user) {
        this.setServiceController(serviceController);
        this.setCurrentUser(user);
        this.serviceController.addObserver(this);
        this.updateRequests(null);
        this.updateSolvedRequests(null);
    }

    //REQUESTS OBSERVER METHODS
    public void deleteRequestObserverMethod(Request request) {
        this.requests.remove(request);
    }
    public final Map<OperationType, Method> mapRequestsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, null);
            put(OperationType.DELETE, RequestsController.class.getMethod("deleteRequestObserverMethod", Request.class));
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateRequests(Event event) {
        if(event == null) {
            System.out.println("load all data for requests table");
            this.requests.clear();
            Iterable<Request> requests = this.serviceController.getAllRequests(this.currentUser);
            this.setRequests(requests);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            System.out.println(operationType.toString() + " Request executed successfully");
            this.mapRequestsOperations.get(operationType).invoke(this, event.getObject());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SOLVED REQUESTS OBSERVER METHODS
    public void addSolvedRequestObserverMethod(Request request) {
        this.solvedRequests.add(request);
    }
    public final Map<OperationType, Method> mapSolvedRequestsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, RequestsController.class.getMethod("addSolvedRequestObserverMethod", Request.class));
            put(OperationType.DELETE, null);
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateSolvedRequests(Event event) {
        if(event == null) {
            System.out.println("load all data for solved requests table");
            this.solvedRequests.clear();
            Iterable<Request> requests = this.serviceController.getAllAnsweredRequests(this.currentUser);
            this.setSolvedRequests(requests);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            mapSolvedRequestsOperations.get(operationType).invoke(this, event.getObject());
            System.out.println(operationType.toString() + " Solved request executed successfully");
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    public void setServiceController(Controller serviceController) {
        this.serviceController = serviceController;
    }
    private void setRequests(Iterable<Request> requests) {
        requests.forEach( req -> this.requests.add(req));
    }
    private void setSolvedRequests(Iterable<Request> requests) {
        requests.forEach( req -> this.solvedRequests.add(req));
    }

    @Override
    public void updateFriends(Event event) {
    }
    @Override
    public void updateUsers(Event event) {
    }
    @Override
    public void updateMessages(Event event) {
    }
    @Override
    public void updateGroups(Event event) {
    }
    @Override
    public void updateGroupMessages(Event event) {
    }
}
