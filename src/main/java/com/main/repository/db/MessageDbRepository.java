package com.main.repository.db;

import com.main.model.GroupMessage;
import com.main.model.Message;
import com.main.model.User;
import com.main.model.validators.Validator;
import com.main.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

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
        String sqlSelectMsg = "select * from messages where id=?";
        String sqlSelectDestination = "select * from source_destination where message_id=?";
        List<User> destinationList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelectMsg = connection.prepareStatement(sqlSelectMsg);
            PreparedStatement psSelectDest = connection.prepareStatement(sqlSelectDestination)){

            psSelectMsg.setLong(1,id);
            ResultSet resultSet = psSelectMsg.executeQuery();
            if(resultSet.next()) {
                Long sourceId = resultSet.getLong("source_id");
                User source = new User(sourceId,null,null,null, null);
                String messageText = resultSet.getString("message_text");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                long repliedMessageId = resultSet.getLong("replied_message_id");
                psSelectDest.setLong(1,id);
                resultSet = psSelectDest.executeQuery();
                while(resultSet.next()){
                    Long destinationId = resultSet.getLong("destination_id");
                    User destination = new User(destinationId,null,null,null, null);
                    destinationList.add(destination);
                }
                Message replied = null;
                if(repliedMessageId != 0){
                    replied = new Message(null,null,null,null);
                    replied.setId(repliedMessageId);
                }
                Message msg = new Message(source,destinationList,messageText,date, replied);
                msg.setId(id);
                return msg;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public Message findMessageById(Long id){
        Message msg = this.findOneById(id);
        Message replied;
        if(msg != null && msg.getRepliedMessage() != null){
            Long repliedMsgId = msg.getRepliedMessage().getId();
            replied = this.findOneById(repliedMsgId);
            msg.setRepliedMessage(replied);
        }
        return msg;
    }

    public GroupMessage findGroupOneById(Long messageId) {
        if (messageId == null)
            throw new IllegalArgumentException("id must be not null");
        String sqlSelectMsg = "select * from group_messages where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelectMsg = connection.prepareStatement(sqlSelectMsg)){
            psSelectMsg.setLong(1,messageId);
            ResultSet resultSet = psSelectMsg.executeQuery();
            if(resultSet.next()) {
                Long sourceId = resultSet.getLong("source_id");
                User source = new User(sourceId,null,null,null, null);
                String messageText = resultSet.getString("message_text");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                long repliedMessageId = resultSet.getLong("replied_message_id");
                long groupId = resultSet.getLong("id_group");
                GroupMessage replied = null;
                if(repliedMessageId != 0){
                    replied = new GroupMessage(null,null,null,null);
                    replied.setId(repliedMessageId);
                }
                GroupMessage msg = new GroupMessage(source, groupId, messageText, date, replied);
                msg.setId(messageId);
                return msg;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public GroupMessage findGroupMessageById(Long messageId){
        GroupMessage msg = this.findGroupOneById(messageId);
        GroupMessage replied;
        if(msg != null && msg.getRepliedMessage() != null) {
            Long repliedMsgId = msg.getRepliedMessage().getId();
            replied = this.findGroupOneById(repliedMsgId);
            msg.setRepliedMessage(replied);
        }
        return msg;
    }

    public Iterable<Message> findAllMessagesBySource(Long sourceId){
        Set<Message> messages = new HashSet<>();
        User source = new User(sourceId, null, null, null, null);
        String sqlSelectMsg = "select * from messages where source_id=? order by id";
        String sqlSelectDest = "select * from source_destination where source_id=? order by message_id";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelectMsg = connection.prepareStatement(sqlSelectMsg);
            PreparedStatement psSelectDest = connection.prepareStatement(sqlSelectDest)){

            psSelectMsg.setLong(1,sourceId);
            psSelectDest.setLong(1,sourceId);
            ResultSet messageSet = psSelectMsg.executeQuery();
            ResultSet destinationSet = psSelectDest.executeQuery();
            while(messageSet.next()){
                long id = messageSet.getLong("id");
                String messageText = messageSet.getString("message_text");
                LocalDateTime date = LocalDateTime.parse(messageSet.getString("date"));
                long repliedMessageId = messageSet.getLong("replied_message_id");
                List<User> destinationList = new ArrayList<>();
                while(destinationSet.next() && id == destinationSet.getLong("message_id")){
                    Long destinationId = destinationSet.getLong("destination_id");
                    User destination = new User(destinationId,null,null,null, null);
                    destinationList.add(destination);
                }
                Message replied = null;
                if(repliedMessageId != 0){
                    replied = new Message(null,null,null,null);
                    replied.setId(repliedMessageId);
                }
                Message msg = new Message(source,destinationList,messageText,date,replied);
                msg.setId(id);
                messages.add(msg);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return messages;
    }

    public Set<Message> findConversation(Long id1, Long id2){
        Set<Message> messages = new TreeSet<>();
        String sqlSelectMessage =
                "select M.id,M.source_id,SD.destination_id,M.message_text,M.date,M.replied_message_id " +
                        "from messages M, source_destination SD " +
                        "where M.id = SD.message_id and (SD.source_id=? and SD.destination_id=? or SD.source_id=? and SD.destination_id=?) " +
                        "order by id";
        try(Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement psSelectMessage = connection.prepareStatement(sqlSelectMessage)){

            psSelectMessage.setLong(1,id1);
            psSelectMessage.setLong(2,id2);
            psSelectMessage.setLong(3,id2);
            psSelectMessage.setLong(4,id1);
            ResultSet resultSet = psSelectMessage.executeQuery();
            while(resultSet.next()){
                Long messageId = resultSet.getLong("id");
                Long sourceId = resultSet.getLong("source_id");
                User source = new User(sourceId,null,null,null, null);
                Long destinationId = resultSet.getLong("destination_id");
                User destination = new User(destinationId,null,null,null, null);
                String messageText = resultSet.getString("message_text");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                long repliedMessageId = resultSet.getLong("replied_message_id");
                Message replied = null;
                if(repliedMessageId != 0){
                    replied = new Message(null,null,null,null);
                    replied.setId(repliedMessageId);
                }
                List<User> destinationList= new ArrayList<>();
                destinationList.add(destination);
                Message msg = new Message(source,destinationList,messageText,date, replied);
                msg.setId(messageId);
                messages.add(msg);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return messages;
    }

    public Set<GroupMessage> findGroupConversation( Long groupId) {
        Set<GroupMessage> messages = new TreeSet<>();
        String sqlSelect = "select * from group_messages where id_group=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)) {
            psSelect.setLong(1, groupId);
            ResultSet resultSet = psSelect.executeQuery();
            while(resultSet.next()) {
                Long messageId = resultSet.getLong("id");
                Long sourceId = resultSet.getLong("source_id");
                User source = new User(sourceId,null,null,null, null);
                String messageText = resultSet.getString("message_text");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                long repliedMessageId = resultSet.getLong("replied_message_id");
                GroupMessage replied = null;
                if(repliedMessageId != 0){
                    replied = new GroupMessage(null,null,null,null);
                    replied.setId(repliedMessageId);
                }
                GroupMessage msg = new GroupMessage(source,groupId,messageText,date, replied);
                msg.setId(messageId);
                messages.add(msg);
            }
        } catch( SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public GroupMessage save(GroupMessage entity) {
        String sqlInsert = "insert into group_messages (source_id, message_text," +
                "date, replied_message_id, id_group) values (?,?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psInsert = connection.prepareStatement(sqlInsert)){
            psInsert.setLong(1, entity.getSource().getId());
            psInsert.setString(2, entity.getMessageText());
            psInsert.setString(3, entity.getDate().toString());
            GroupMessage replied = entity.getRepliedMessage();
            psInsert.setNull(4, INTEGER);
            if(replied != null)
                psInsert.setLong(4, entity.getRepliedMessage().getId());
            psInsert.setLong(5, entity.getIdGroup());
            psInsert.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message save(Message entity) {
        this.validator.validate(entity);
        String sqlInsert = "insert into messages (source_id, message_text," +
                "date, replied_message_id) values (?, ?, ?, ?)";
        String insertDestination ="insert into source_destination (source_id, destination_id,message_id)" +
                "values(?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psInsert = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psDest = connection.prepareStatement(insertDestination)) {
            //psMsg.setLong(1, entity.getId());
            psInsert.setLong(1, entity.getSource().getId());
            psInsert.setString(2, entity.getMessageText());
            psInsert.setString(3,entity.getDate().toString());
            Message replied = entity.getRepliedMessage();
            psInsert.setNull(4, INTEGER);
            if(replied != null)
                psInsert.setLong(4, entity.getRepliedMessage().getId());
            psInsert.executeUpdate();
            ResultSet rs = psInsert.getGeneratedKeys();
            rs.next();
            for(User u : entity.getDestination()){
                psDest.setLong(1, entity.getSource().getId());
                psDest.setLong(2, u.getId());
                psDest.setLong(3, rs.getLong(1));
                psDest.executeUpdate();
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
            psUpdate.setLong(2,entity.getId());
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

    @Override
    public Iterable<Message> findAll(Long source) {
        return findAllMessagesBySource(source);
    }

}
