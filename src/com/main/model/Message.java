package com.main.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private final User source;
    private final List<User> destionation;
    private String messageText;
    private final LocalDateTime date;
    private Message repliedMessage;

    public Message(Long id, User source, List<User> destination,
                   String messageText, LocalDateTime date ){
        this.source = source;
        this.destionation = destination;
        this.messageText = messageText;
        this.date =date;
        super.setId(id);
    }

    public Message(Long id, User source, List<User> destination, String messageText,
                   LocalDateTime date, Message repliedMessage){
        this(id, source, destination, messageText, date);
        this.repliedMessage = repliedMessage;
    }

    public User getSource(){
        return source;
    }

    public List<User> getDestination(){
        return destionation;
    }

    public String getMessageText(){
        return messageText;
    }
    public void setMessageText(String messageText){
        this.messageText = messageText;
    }

    public LocalDateTime getDate(){
        return date;
    }

    public Message getRepliedMessage(){
        return repliedMessage;
    }
    public void setRepliedMessage(Message repliedMessage){
        if(this.equals(repliedMessage))
            return;
        this.repliedMessage = repliedMessage;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = date.format(formatter);
        String msg = source.getFirstName() + " " +
                source.getLastName() + " (" +
                formattedDate + ") :" +
                messageText;
       if(repliedMessage != null)
           msg = msg + "\n\t[in reply to " +
                   repliedMessage.getSource().getFirstName() + " " +
                   repliedMessage.getSource().getLastName() +
                   " :" + repliedMessage.getMessageText() + "]";
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSource(), getMessageText(), getDate());
    }

}
