package com.main;

import com.main.controller.Controller;
import com.main.model.SocialEvent;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.observer.Observer;
import com.main.utils.observer.OperationType;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventsController implements Observer {
    Controller serviceController;
    User currentUser;
    ObservableList<SocialEvent> socialEvents = FXCollections.observableArrayList();
    SocialEvent displayedEvent;

    @FXML TableColumn<SocialEvent, String> event_name;
    @FXML TableView<SocialEvent> socialEventsTable;

    @FXML ImageView notificationOn;
    @FXML ImageView notificationOff;

    @FXML Button goingButton;
    @FXML Button notGoingButton;

    @FXML VBox body;
    @FXML Label eventNameLabel;
    @FXML Label hostNameLabel;
    @FXML Label dateLabel;

    @FXML
    public void initialize() {
        body.setVisible(false);
        goingButton.setVisible(false);
        goingButton.managedProperty().bind(goingButton.visibleProperty());
        goingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(displayedEvent != null) {
                serviceController.addParticipantToEvent(displayedEvent, currentUser);
            }
        });
        notGoingButton.setVisible(false);
        notGoingButton.managedProperty().bind(notGoingButton.visibleProperty());
        notGoingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(displayedEvent != null) {
                serviceController.removeParticipantFromEvent(displayedEvent, currentUser);
            }
        });
        notificationOn.setVisible(false);
        notificationOn.managedProperty().bind(notificationOn.visibleProperty());
        notificationOn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(displayedEvent != null) {
                serviceController.addNotification(displayedEvent, currentUser);
                this.showEvent(displayedEvent);
            }
        });
        notificationOff.setVisible(false);
        notificationOff.managedProperty().bind(notificationOff.visibleProperty());
        notificationOff.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(displayedEvent != null) {
                serviceController.removeNotification(displayedEvent, currentUser);
                this.showEvent(displayedEvent);
            }
        });

        event_name.setCellValueFactory(param ->
                new ReadOnlyStringWrapper( param.getValue().getName()));
        socialEventsTable.setItems(socialEvents);
        socialEventsTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                SocialEvent event = socialEventsTable.getSelectionModel().getSelectedItem();
                if(event != null) {
                    System.out.println("Double-clicked on " + event);
                    showEvent(event);
                }
            }
        });
    }

    public void afterLoad(Controller serviceController, User user) {
        this.serviceController = serviceController;
        this.serviceController.addObserver(this);
        this.currentUser = user;
        this.updateEvents(null);
    }

    public void showEvent(SocialEvent event) {
        body.setVisible(true);
        displayedEvent = event;
        Long hostId = event.getIdsParticipants().get(0);
        this.eventNameLabel.setText(event.getName());
        User host = serviceController.findUserById(hostId);
        this.hostNameLabel.setText(host.getFirstName() + " " + host.getLastName());
        this.dateLabel.setText(event.getDate().toString());
        if(currentUser.getId().equals(hostId)) {
            goingButton.setVisible(false);
            notGoingButton.setVisible(false);
            notificationOn.setVisible(false);
            notificationOff.setVisible(false);
        }
        else {
            boolean going = event.getIdsParticipants().contains(currentUser.getId());
                notGoingButton.setVisible(going);
                goingButton.setVisible(!going);
            boolean notify = serviceController.findNotificationOfParticipant(event, currentUser);
                notificationOff.setVisible(going & notify);
                notificationOn.setVisible(going & !notify);
        }
    }

    public void addSocialEventObserverMethod(SocialEvent ev) {
        this.socialEvents.add(ev);
        this.showEvent(ev);
    }
    public void updateSocialEventObserverMethod(SocialEvent ev) {
        for(int i = 0; i<this.socialEvents.size(); ++i) {
            if(this.socialEvents.get(i).getId().equals(ev.getId()) ) {
                this.socialEvents.set(i, ev);
                this.showEvent(ev);
                return;
            }
        }
    }
    public final Map<OperationType, Method> mapSocialEventsOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, EventsController.class.getMethod("addSocialEventObserverMethod", SocialEvent.class));
            put(OperationType.DELETE, null);
            put(OperationType.UPDATE, EventsController.class.getMethod("updateSocialEventObserverMethod", SocialEvent.class));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateEvents(Event event) {
        if(event == null) {
            System.out.println("load all data for requests table");
            this.socialEvents.clear();
            Iterable<SocialEvent> events = this.serviceController.showAllEvents();
            this.setSocialEvents(events);
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            mapSocialEventsOperations.get(operationType).invoke(this, event.getObject());
            System.out.println(operationType.toString() + " SocialEvent executed succesfully");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void setSocialEvents(Iterable<SocialEvent> events) {
        events.forEach(ev -> this.socialEvents.add(ev));
    }

    @Override
    public void updateFriends(Event event) {
    }

    @Override
    public void updateRequests(Event event) {
    }

    @Override
    public void updateSolvedRequests(Event event) {
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
