package com.main;

import com.main.controller.Controller;
import com.main.model.Message;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.observer.Observer;
import com.main.utils.observer.OperationType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController implements Observer {

    private Controller serviceController;
    private User currentUser, destination;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    @FXML
    Button back;
    @FXML
    Label destinationLabel;
    @FXML
    ListView<Message> messagesView;
    @FXML
    TextArea textarea;
    @FXML
    Button reset;
    @FXML
    Button send;

    @FXML
    public void initialize() {
        this.messagesView.setCellFactory(param -> new ListViewCell(currentUser.getId()) );
        this.messagesView.setItems(messages);
        textarea.setWrapText(true);
    }

    public void afterLoad(Controller serviceController, User currentUser, User destination) {
        this.serviceController = serviceController;
        this.currentUser = currentUser;
        this.destination = destination;
        this.serviceController.addObserver(this);
        this.destinationLabel.setText(destination.getFirstName() + " " + destination.getLastName());
        this.updateMessages(null);
    }


    public void addMessageObserverMethod(Message msg) {
        this.messages.add(msg);
    }
    public final Map<OperationType, Method> mapMessagesOperations = new HashMap<>(){{
        try {
            put(OperationType.ADD, ChatController.class.getMethod("addMessageObserverMethod", Message.class));
            put(OperationType.DELETE, null);
            put(OperationType.UPDATE, null);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }};
    @Override
    public void updateMessages(Event event) {
        if(event == null) {
            this.messages.clear();
            this.serviceController.
                    getConversation(currentUser.getUsername(), destination.getUsername()).
                    forEach(messages::add);
            this.scrollDown();
            return;
        }
        OperationType operationType = event.getOperationType();
        try {
            mapMessagesOperations.get(operationType).invoke(this, event.getObject());
            this.scrollDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void sendMessage() {
        String textMessage = this.textarea.getText();
        List<String> destinationUsername = new ArrayList<>();
        destinationUsername.add(destination.getUsername());
        Message replied = this.messagesView.getSelectionModel().getSelectedItem();
        this.serviceController.sendMessage(currentUser, destinationUsername, textMessage,
                LocalDateTime.now(), replied == null ? 0L : replied.getId());
        this.textarea.clear();
        this.resetReply();
    }

    @FXML
    void resetReply() {
        this.messagesView.getSelectionModel().clearSelection();
    }

    public void scrollDown() {
        messagesView.scrollTo(messages.size() - 1);
    }

    static final class ListViewCell extends ListCell<Message> {
        private final Long idCurrentUser;

        public ListViewCell(Long userId) {
            this.idCurrentUser = userId;
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                VBox vBox = new VBox();
                //Messages from currentUser
                if (item.getSource().getId().equals(idCurrentUser)) {

                    vBox.setAlignment(Pos.CENTER_RIGHT);
                    Label label = styleLabel(item.getMessageText());
                    label.setAlignment(Pos.CENTER_RIGHT);
                    var reply=item.getRepliedMessage();
                    if(reply==null)
                        vBox.getChildren().add(label);
                    else
                    {
                        var user=reply.getSource();
                        Label textReply=new Label("You replied to: " + user.getFirstName() + " " +
                                user.getLastName());
                        textReply.setAlignment(Pos.CENTER_RIGHT);
                        Label replyLabel=styleReplyLabel(reply.getMessageText());
                        replyLabel.setAlignment(Pos.CENTER_RIGHT);
                        vBox.getChildren().addAll(textReply,replyLabel,label);
                    }
                }
                //Messages from other user
                else{
                    vBox.setAlignment(Pos.CENTER_LEFT);
                    Label label = styleLabel(item.getMessageText());
                    label.setAlignment(Pos.CENTER_LEFT);
                    var reply=item.getRepliedMessage();
                    if(reply==null)
                        vBox.getChildren().add(label);
                    else
                    {
                        var user=reply.getSource();
                        Label textReply= new Label("Reply to: " + user.getFirstName() + " " +
                                user.getLastName());
                        textReply.setAlignment(Pos.CENTER_LEFT);
                        Label replyLabel=styleReplyLabel(reply.getMessageText());
                        replyLabel.setAlignment(Pos.CENTER_LEFT);
                        vBox.getChildren().addAll(textReply,replyLabel,label);
                    }
                }
                setGraphic(vBox);
            }
        }
        private Label styleLabel(String msg){
            var label=new Label(msg);
            label.setMinWidth(50);
            label.setMinHeight(50);
            label.setMaxWidth(280);
            label.setWrapText(true);
            label.setStyle("-fx-hgap: 10px;" +
                    "    -fx-padding: 20px;" +
                    "" +
                    "    -fx-background-color: #997dff;" +
                    "    -fx-background-radius: 25px;" +
                    "" +
                    "    -fx-border-radius: 25px;" +
                    "    -fx-border-width: 5px;" +
                    "    -fx-border-color: #997dff;" +
                    "-fx-text-fill: white;");
            return label;
        }

        private Label styleReplyLabel(String msg){
            var label=new Label(msg);
            label.setMinWidth(50);
            label.setMinHeight(50);
            label.setMaxWidth(240);
            label.setStyle("-fx-hgap: 5px;" +
                    "    -fx-padding: 5px;" +
                    "" +
                    "    -fx-background-color: #bbadff;" +
                    "    -fx-background-radius: 13px;" +
                    "" +
                    "    -fx-border-radius: 13px;" +
                    "    -fx-border-width: 5px;" +
                    "    -fx-border-color: #bbadff;" +
                    "-fx-text-fill: white;");
            return label;
        }
    }

    @Override
    public void updateGroups(Event event) {
        //nothing
    }

    @Override
    public void updateGroupMessages(Event event) {
        //nothing
    }

    @Override
    public void updateFriends(Event event) {
        //nothing
    }

    @Override
    public void updateRequests(Event event) {
        //nothing
    }

    @Override
    public void updateSolvedRequests(Event event) {
        //nothing
    }

    @Override
    public void updateUsers(Event event) {
        //nothing
    }
}
