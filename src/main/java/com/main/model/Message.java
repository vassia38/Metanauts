package com.main.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> implements Comparable<Message>{
    private User source;
    private final List<User> destination;
    private String messageText;
    private final LocalDateTime date;
    private Message repliedMessage;

    public Message(User source, List<User> destination,
                   String messageText, LocalDateTime date ){
        this.source = source;
        this.destination = destination;
        this.messageText = messageText;
        this.date =date;
    }

    public Message(User source, List<User> destination, String messageText,
                   LocalDateTime date, Message repliedMessage){
        this(source, destination, messageText, date);
        this.repliedMessage = repliedMessage;
    }

    public User getSource(){
        return source;
    }
    public void setSource(User source){
        this.source = source;
    }

    public List<User> getDestination(){
        return destination;
    }

    public void setDestination(List<User> newDestinationList){
        this.destination.clear();
        this.destination.addAll(newDestinationList);
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
        String firstname = null,lastname = null;
        String formattedDate = null;

        if(source != null) {
            firstname = source.getFirstName();
            lastname = source.getLastName();
        }
        if(date != null){
            formattedDate = date.format(formatter);
        }
        return "[msg id " + this.getId() + "] " + firstname + " " +
                lastname + " (" +
                formattedDate + ") :\n" +
                messageText + repliedToString();
    }

    private String repliedToString() {
        if(repliedMessage == null || repliedMessage.getSource() == null)
            return "";
        return "\n\t{in reply to " + "[msg id " + repliedMessage.getId() + "] " +
                repliedMessage.getSource().getFirstName() + " " +
                repliedMessage.getSource().getLastName() +
                " :\n\t " + repliedMessage.getMessageText() + "}";
    }

    public String getTextMessage() {
        return this.messageText;
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


    @Override
    public int compareTo(Message o) {
        if (this.equals(o)) return 0;
        return getId() < o.getId() ? -1 : 1;
    }
}
