package com.main.repository.db;

import com.main.model.Request;
import com.main.model.Tuple;
import com.main.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RequestDbRepository implements Repository<Tuple<Long,Long>, Request> {
    private final String url;
    private final String username;
    private final String password;

    public RequestDbRepository(String url,String username,String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Request findOneById(Tuple<Long, Long> id) {
        if (id==null)
            throw new IllegalArgumentException("entity must be not null");
        String sqlSelect = "select * from requests where id1=? and id2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setLong(1,id.getLeft());
            psSelect.setLong(2,id.getRight());
            ResultSet resultSet = psSelect.executeQuery();
            if(resultSet.next()) {
               String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("dateSent"));
                return new Request(id.getLeft(),id.getRight(),status,date);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Request> findAll() {
        Set<Request> requests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from requests");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("dateSent"));
                Request request = new Request(id1, id2, status, date);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Iterable<Request> findAll(Tuple<Long, Long> id) {
        if(id == null) {
            throw new IllegalArgumentException("entity must be not null");
        }
        Set<Request> requests = new HashSet<>();
        String sqlSelect = "SELECT * from requests where id2=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psSelect = connection.prepareStatement(sqlSelect)) {
            psSelect.setLong(1, id.getRight());
            ResultSet resultSet = psSelect.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("dateSent"));
                Request request = new Request(id1, id2, status, date);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Request save(Request entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        if(entity.equals(this.findOneById(entity.getId()))) {
            return entity;
        }
        String sqlInsert = "insert into requests values(?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psInsert = connection.prepareStatement(sqlInsert)){
            psInsert.setLong(1,entity.getId().getLeft());
            psInsert.setLong(2,entity.getId().getRight());
            psInsert.setString(3,entity.getStatus());
            psInsert.setString(4,entity.getDate().toString());
            psInsert.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Request delete(Tuple<Long, Long> id) {
        Request found = this.findOneById(id);
        if(found == null)
            return null;
        String sqlDelete = "delete from requests where id1=? and id2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psDelete = connection.prepareStatement(sqlDelete)){
            Long id1 = id.getLeft();
            Long id2 = id.getRight();
            psDelete.setLong(1,id1);
            psDelete.setLong(2,id2);
            psDelete.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return found;
    }

    @Override
    public Integer size() {
        String sqlCount = "select count(*) from requests";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psCount = connection.prepareStatement(sqlCount)){
            ResultSet resultSet = psCount.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Request update(Request entity) {
        Request oldState = this.findOneById(entity.getId());
        if(oldState == null)
            return null;
        String sqlUpdate = "update requests set status=? where id1=? and id2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)){
            psUpdate.setString(1,entity.getStatus());
            psUpdate.setLong(2,entity.getId().getLeft());
            psUpdate.setLong(3,entity.getId().getRight());
            psUpdate.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return oldState;
    }
}
