package com.main;

import com.main.controller.Controller;
import com.main.model.Message;
import com.main.model.User;
import com.main.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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
    Button send;

    @FXML
    public void initialize() {
        this.messagesView.setCellFactory(param -> new ListViewCell(currentUser.getId()) );
        this.messagesView.setItems(messages);
    }

    public void afterLoad(Controller serviceController, User currentUser, User destination) {
        this.serviceController = serviceController;
        this.currentUser = currentUser;
        this.destination = destination;
        this.serviceController.addObserver(this);

        this.destinationLabel.setText(destination.getFirstName() + " " + destination.getLastName());
        this.updateMessages();
    }

    @Override
    public void updateMessages() {
        this.messages.removeAll();
        this.serviceController.
                getConversation(currentUser.getUsername(), destination.getUsername()).
                forEach(messages::add);
    }

    static final class ListViewCell extends ListCell<Message> {
        private final Long idUser;

        public ListViewCell(Long userId) {
            this.idUser = userId;
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                VBox vBox = new VBox();
                if (item.getSource().getId().equals(idUser)) {

                    vBox.setAlignment(Pos.CENTER_RIGHT);
                    Label label = styleLabel(item.getTextMessage());
                    label.setAlignment(Pos.CENTER_RIGHT);
                    var reply=item.getRepliedMessage();
                    if(reply==null)
                        vBox.getChildren().addAll(label);
                    else
                    {
                        var user=reply.getSource();
                        Label textReply=new Label("You replied to: " + user.getFirstName() + " " +
                                user.getLastName());
                        textReply.setAlignment(Pos.CENTER_RIGHT);
                        Label replyLabel=styleReplyLabel(reply.getTextMessage());
                        replyLabel.setAlignment(Pos.CENTER_RIGHT);
                        vBox.getChildren().addAll(textReply,replyLabel,label);
                    }
                }
                else{
                    vBox.setAlignment(Pos.CENTER_LEFT);
                    Label label = styleLabel(item.getTextMessage());
                    label.setAlignment(Pos.CENTER_LEFT);
                    var reply=item.getRepliedMessage();
                    if(reply==null)
                        vBox.getChildren().addAll(label);
                    else
                    {
                        var user=reply.getSource();
                        Label textReply= new Label("Reply to: " + user.getFirstName() + " " +
                                user.getLastName());
                        textReply.setAlignment(Pos.CENTER_LEFT);
                        Label replyLabel=styleReplyLabel(reply.getTextMessage());
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
    public void updateFriends() {
        //nothing
    }

    @Override
    public void updateRequests() {
        //nothing
    }

    @Override
    public void updateUsers() {
        //nothing
    }
}
