package com.main.view;

import com.main.model.Message;
import com.main.service.MessageService;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class ChatController {
    final class ListViewCell extends ListCell<Message> {
        private Long idUser;
        private MessageService messageService;
        public ListViewCell(Long userId,MessageService messageService) {
            this.idUser = userId;
            this.messageService=messageService;
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                if (item.getFrom().getId() == idUser) {

                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER_RIGHT);
                    Label label = styleLabel(item.getGUIMessage());
                    label.setAlignment(Pos.CENTER_RIGHT);
                    var reply=item.getReply();
                    if(reply==null)
                        vBox.getChildren().addAll(label);
                    else
                    {
                        var user=reply.getFrom();
                        Label textReply=new Label("You replied to: "+user.getFullName());
                        textReply.setAlignment(Pos.CENTER_RIGHT);
                        Label replyLabel=styleReplyLabel(reply.getGUIMessage());
                        replyLabel.setAlignment(Pos.CENTER_RIGHT);
                        vBox.getChildren().addAll(textReply,replyLabel,label);
                    }
                    setGraphic(vBox);
                }
                else{
                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER_LEFT);
                    Label label = styleLabel(item.getGUIMessage());
                    label.setAlignment(Pos.CENTER_LEFT);
                    var reply=item.getReply();
                    if(reply==null)
                        vBox.getChildren().addAll(label);
                    else
                    {
                        var user=reply.getFrom();
                        Label textReply=new Label("Reply to: "+user.getFullName());
                        textReply.setAlignment(Pos.CENTER_LEFT);
                        Label replyLabel=styleReplyLabel(reply.getGUIMessage());
                        replyLabel.setAlignment(Pos.CENTER_LEFT);
                        vBox.getChildren().addAll(textReply,replyLabel,label);
                    }
                    setGraphic(vBox);
                }
            }
        }


        private Label styleLabel(String msg){
            var label=new Label(msg);
            label.setMinWidth(50);
            label.setMinHeight(50);
            label.setStyle("-fx-hgap: 10px;" +
                    "    -fx-padding: 20px;" +
                    "" +
                    "    -fx-background-color: #2969c0;" +
                    "    -fx-background-radius: 25px;" +
                    "" +
                    "    -fx-border-radius: 25px;" +
                    "    -fx-border-width: 5px;" +
                    "    -fx-border-color: black;" +
                    "-fx-text-fill: white;" +
                    "    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);");
            return label;
        }

        private Label styleReplyLabel(String msg){
            var label=new Label(msg);
            label.setMinWidth(50);
            label.setMinHeight(50);
            label.setStyle("-fx-hgap: 5px;" +
                    "    -fx-padding: 5px;" +
                    "" +
                    "    -fx-background-color: #87a3c9;" +
                    "    -fx-background-radius: 13px;" +
                    "" +
                    "    -fx-border-radius: 13px;" +
                    "    -fx-border-width: 5px;" +
                    "    -fx-border-color: #272d36;" +
                    "-fx-text-fill: black;" +
                    "    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);");
            return label;
        }
    }
}
