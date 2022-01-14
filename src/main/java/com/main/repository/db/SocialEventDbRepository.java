package com.main.repository.db;

import com.main.model.SocialEvent;
import com.main.model.User;
import com.main.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SocialEventDbRepository implements Repository<Long, SocialEvent> {
    private final String url;
    private final String username;
    private final String password;

    public SocialEventDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public SocialEvent findOneById(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must not be null");
        String sqlEventSelect = "select * from socialevents where id=?";
        String sqlParticipantsSelect = "select * from socialevents_participants where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psEventSelect = connection.prepareStatement(sqlEventSelect);
            PreparedStatement psParticipantsSelect = connection.prepareStatement(sqlParticipantsSelect)){
            psEventSelect.setLong(1, id);
            psParticipantsSelect.setLong(1, id);
            ResultSet resultSet = psEventSelect.executeQuery();
            if(resultSet.next()) {
                String name = resultSet.getString("name");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                String coverphoto = resultSet.getString("coverphoto");
                List<Long> idsParticipants = new ArrayList<>();
                resultSet = psParticipantsSelect.executeQuery();
                while(resultSet.next()) {
                    Long idParticipant = resultSet.getLong("id_user");
                    idsParticipants.add(idParticipant);
                }
                System.out.println("[findOneById] " + id + " " + name);
                return new SocialEvent(id, name, date, idsParticipants, coverphoto);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<SocialEvent> findAll() {
        Set<SocialEvent> events = new HashSet<>();
        String sqlEventsIdsSelect = "select * from socialevents";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psEventsIdsSelect = connection.prepareStatement(sqlEventsIdsSelect)) {
            ResultSet resultSet = psEventsIdsSelect.executeQuery();
            while(resultSet.next()) {
                Long idEvent = resultSet.getLong("id");
                SocialEvent event = this.findOneById(idEvent);
                if(event != null)
                    events.add(event);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Iterable<SocialEvent> findAll(Long userId) {
        if (userId==null)
            throw new IllegalArgumentException("User id must not be null");
        Set<SocialEvent> events = new HashSet<>();
        String sqlEventsIdsSelect = "select * from socialevents_participants where id_user=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psEventsIdsSelect = connection.prepareStatement(sqlEventsIdsSelect)) {
            psEventsIdsSelect.setLong(1, userId);
            ResultSet resultSet = psEventsIdsSelect.executeQuery();
            while(resultSet.next()) {
                Long idEvent = resultSet.getLong("id");
                SocialEvent event = this.findOneById(idEvent);
                if(event != null)
                    events.add(event);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return events;
    }


    public Iterable<SocialEvent> findAll(String name) {
        if (name==null)
            throw new IllegalArgumentException("Event name must not be null");
        Set<SocialEvent> events = new HashSet<>();
        String sqlEventsIdsSelect = "select * from socialevents_participants where name=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psEventsIdsSelect = connection.prepareStatement(sqlEventsIdsSelect)) {
            psEventsIdsSelect.setString(1, name);
            ResultSet resultSet = psEventsIdsSelect.executeQuery();
            while(resultSet.next()) {
                Long idEvent = resultSet.getLong("id");
                SocialEvent event = this.findOneById(idEvent);
                if(event != null)
                    events.add(event);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public SocialEvent save(SocialEvent event) {
        if(event == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        String sqlEventInsert = "insert into socialevents (name, date, coverphoto) values (?, ?, ?)";
        String sqlEventParticipantInsert = "insert into socialevents_participants (id, id_user, notification) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psEventInsert = connection.prepareStatement(sqlEventInsert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psEventParticipantInsert = connection.prepareStatement(sqlEventParticipantInsert)) {
            psEventInsert.setString(1, event.getName());
            psEventInsert.setString(2, event.getDate().toString());
            psEventInsert.setString(3, event.getCoverphoto());
            psEventInsert.executeUpdate();
            ResultSet rs = psEventInsert.getGeneratedKeys();
            if(event.getIdsParticipants().size() > 0 && rs.next()) {
                long idEvent = rs.getLong(1);
                System.out.println(idEvent);
                psEventParticipantInsert.setLong(1, idEvent);
                for(Long idUser : event.getIdsParticipants()) {
                    psEventParticipantInsert.setLong(2, idUser);
                    psEventParticipantInsert.executeUpdate();
                }
                return new SocialEvent(idEvent, event.getName(), event.getDate(), event.getIdsParticipants(), event.getCoverphoto());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addParticipant(Long idEvent, Long idUser, Integer notification) {
        if(idEvent == null)
            throw new IllegalArgumentException("Event id must not be null");
        if (idUser==null)
            throw new IllegalArgumentException("User id must not be null");
        String sqlInsert = "insert into socialevents_participants(id, id_user, notification) values(?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
            psInsert.setLong(1, idEvent);
            psInsert.setLong(2, idUser);
            psInsert.setInt(3, notification);
            psInsert.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SocialEvent delete(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        SocialEvent found = this.findOneById(id);
        if(found == null)
            return null;
        String sqlDelete = "delete from socialevent where (id=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psDelete = connection.prepareStatement(sqlDelete)){
            psDelete.setLong(1, id);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    public Long removeParticipant(Long idEvent, Long idUser) {
        if (idEvent==null)
            throw new IllegalArgumentException("Event id must be not null");
        SocialEvent found = this.findOneById(idEvent);
        if(found == null)
            return null;
        if (idUser==null)
            throw new IllegalArgumentException("User id must be not null");
        String sqlDelete = "delete from socialevent_participants where (id=?) and (id_user=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psDelete = connection.prepareStatement(sqlDelete)){
            psDelete.setLong(1, idEvent);
            psDelete.setLong(2, idUser);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return idUser;
    }

    public void updateNotification(Long idEvent, Long idUser, Integer notification) {
        SocialEvent event = this.findOneById(idEvent);
        if(event == null)
            return;
        String sqlUpdate = "update socialevents_participants set notification=? where (id=?) and (id_user=?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)){
            psUpdate.setLong(1,notification);
            psUpdate.setLong(2,idEvent);
            psUpdate.setLong(3,idUser);
            psUpdate.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean findParticipantInEvent (Long idEvent, Long idUser) {
        if (idEvent==null)
            throw new IllegalArgumentException("Event id must not be null");
        if (idUser==null)
            throw new IllegalArgumentException("User id must not be null");
        String sqlParticipantsSelect = "select * from socialevents_participants where (id=?) and (id_user=?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psParticipantsSelect = connection.prepareStatement(sqlParticipantsSelect)){
            psParticipantsSelect.setLong(1, idEvent);
            ResultSet resultSet = psParticipantsSelect.executeQuery();
            if(resultSet.next()) {
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer size() {
        return null;
    }

    @Override
    public SocialEvent update(SocialEvent entity) {
        return null;
    }
}
