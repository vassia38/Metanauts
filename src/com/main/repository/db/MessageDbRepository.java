package com.main.repository.db;

import com.main.model.Message;
import com.main.model.User;
import com.main.model.validators.Validator;
import com.main.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;

public class MessageDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOneById(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        String sqlSelect = "select * from messages where id=?";
        List<User> destinationList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setLong(1,id);
            ResultSet resultSet = psSelect.executeQuery();
            User source = null;
            String messageText = null;
            LocalDateTime date = null;
            Long repliedMessageId = null;
            if(resultSet.next()) {
                Long sourceId = resultSet.getLong("source_id");
                source = this.findUser(sourceId);
                Long destinationId = resultSet.getLong("destination_id");
                User destination = this.findUser(destinationId);
                destinationList.add(destination);
                messageText = resultSet.getString("message_text");
                date = LocalDateTime.parse(resultSet.getString("date"));
                repliedMessageId = resultSet.getLong("replied_message_id");
            }
            while(resultSet.next()){
                Long destinationId = resultSet.getLong("destination_id");
                User destination = this.findUser(destinationId);
                destinationList.add(destination);
            }
            if(source == null){
                return null;
            }
            Message replied = null;
            if(repliedMessageId != null)
                replied = new Message(repliedMessageId,null,null,null,null);
            return new Message(id,source,destinationList,messageText,date, replied);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Message findMessageById(Long id){
        Message msg = this.findOneById(id);
        if(msg.getRepliedMessage() != null){
            Long repliedMsgId = msg.getRepliedMessage().getId();
            msg.setRepliedMessage(this.findOneById(repliedMsgId));
        }
        return msg;
    }

    private User findUser(Long id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        String sqlSelect = "select * from users where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setLong(1,id);
            ResultSet resultSet = psSelect.executeQuery();
            if(resultSet.next()) {
                String userName = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return new User(id, userName,firstName, lastName);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message save(Message entity) {
        this.validator.validate(entity);
        String sql = "insert into messages (id, source_id, destination_id, " +
                "message_text, date, replied_message_id) values (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getSource().getId());
            ps.setString(4, entity.getMessageText());
            ps.setString(5,entity.getDate().toString());
            Message replied = entity.getRepliedMessage();
            ps.setNull(6, INTEGER);
            if(replied != null)
                ps.setLong(6, entity.getRepliedMessage().getId());
            for(User user : entity.getDestination()){
                ps.setLong(3,user.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message update(Message entity) {
        this.validator.validate(entity);
        Message oldState = this.findMessageById(entity.getId());
        if(oldState == null)
            return null;
        String sqlUpdate = "update messages set message_text=? where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)){
            psUpdate.setString(1,entity.getMessageText());
            psUpdate.setLong(3,entity.getId());
            psUpdate.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return oldState;
    }

    @Override
    public Message delete(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        Message found = this.findMessageById(id);
        if(found == null)
            return null;
        String sqlDelete = "delete from messages where (id=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psDelete = connection.prepareStatement(sqlDelete)){
            psDelete.setLong(1, id);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    @Override
    public Integer size() {
        String sqlCount = "select count(*) from messages";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psCount = connection.prepareStatement(sqlCount)){
            ResultSet resultSet = psCount.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

}
