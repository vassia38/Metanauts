package com.main.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GroupMessage extends Entity<Long> implements Comparable<GroupMessage>{
    Message message;
    Long idGroup;
    GroupMessage repliedMessage;

    public GroupMessage(User source, Long idGroup, String messageText,
                        LocalDateTime date) {
        this.message = new Message(source, null, messageText, date);
        this.idGroup = idGroup;
    }

    public GroupMessage(User source, Long idGroup, String messageText,
                        LocalDateTime date, GroupMessage repliedMessage) {
        this.message = new Message(source, null, messageText, date);
        this.idGroup = idGroup;
        this.repliedMessage = repliedMessage;
    }

    public void setId(Long id) {
        super.setId(id);
        this.message.setId(id);
    }

    public User getSource() {
        return this.message.getSource();
    }

    public void setSource(User user) {
        this.message.setSource(user);
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public String getMessageText() {
        return this.message.getMessageText();
    }

    public void setMessageText(String messageText){
        this.message.setMessageText(messageText);
    }

    public LocalDateTime getDate(){
        return this.message.getDate();
    }

    public GroupMessage getRepliedMessage(){
        return repliedMessage;
    }

    public void setRepliedMessage(GroupMessage repliedMessage){
        if(this.equals(repliedMessage))
            return;
        this.repliedMessage = repliedMessage;
    }

    private String repliedToString() {
        if(repliedMessage == null || repliedMessage.getSource() == null)
            return "";
        return "\n\t{in reply to " + "[msg id " + repliedMessage.getId() + "] " +
                repliedMessage.getSource().getFirstName() + " " +
                repliedMessage.getSource().getLastName() +
                " :\n\t " + repliedMessage.getMessageText() + "}";
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String firstname = null,lastname = null;
        String formattedDate = null;

        if(this.message.getSource() != null) {
            User source = this.message.getSource();
            firstname = source.getFirstName();
            lastname = source.getLastName();
        }
        if(this.getDate() != null){
            formattedDate = this.getDate().format(formatter);
        }
        return "[msg id " + this.getId() + "] " + firstname + " " +
                lastname + " (" +
                formattedDate + ") :\n" +
                this.getMessageText() + repliedToString();
    }

    @Override
    public int compareTo(GroupMessage o) {
        return this.message.compareTo(o.message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        GroupMessage other = (GroupMessage) o;
        return Objects.equals(this.id, other.id);
    }
}
