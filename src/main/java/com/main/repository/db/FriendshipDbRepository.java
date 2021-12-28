package com.main.repository.db;

import com.main.model.Friendship;
import com.main.model.Tuple;
import com.main.model.validators.Validator;
import com.main.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<Long,Long>,Friendship> {
    private final String url;
    private final String username;
    private final String password;
    Validator<Friendship> validator;
    public FriendshipDbRepository(String url,String username,String password, Validator<Friendship> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOneById(Tuple<Long, Long> id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        String sqlSelect = "select * from friendships where id1=? and id2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setLong(1,id.getLeft());
            psSelect.setLong(2,id.getRight());
            ResultSet resultSet = psSelect.executeQuery();
            if(resultSet.next()) {
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_friendship"));
                return new Friendship(id.getLeft(),id.getRight(),date);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_friendship"));
                Friendship friendship = new Friendship(id1, id2, date);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Iterable<Friendship> findAll(Tuple<Long, Long> id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        Set<Friendship> friendships = new HashSet<>();
        String sqlSelect = "SELECT * from friendships where id1=? or id2=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psSelect = connection.prepareStatement(sqlSelect)) {
            psSelect.setLong(1,id.getLeft());
            psSelect.setLong(2,id.getLeft());
            ResultSet resultSet = psSelect.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_friendship"));
                Friendship friendship = new Friendship(id1, id2, date);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        this.validator.validate(entity);
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        if(entity.equals(this.findOneById(entity.getId()))){
            return entity;
        }
        String sqlInsert = "insert into friendships values(?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement psInsert = connection.prepareStatement(sqlInsert)){
            psInsert.setLong(1,entity.getId().getLeft());
            psInsert.setLong(2,entity.getId().getRight());
            psInsert.setString(3,entity.getDate().toString());
            psInsert.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        Friendship found = this.findOneById(id);
        if(found == null)
            return null;
        String sqlDelete = "delete from friendships where id1=? and id2=?";
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
        String sqlCount = "select count(*) from friendships";
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
    public Friendship update(Friendship entity) {
        this.validator.validate(entity);
        Friendship oldState = this.findOneById(entity.getId());
        if(oldState == null)
            return null;
        String sqlUpdate = "update friendships set date_friendship=? where id1=? and id2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)){
            psUpdate.setString(1,entity.getDate().toString());
            psUpdate.setLong(2,entity.getId().getLeft());
            psUpdate.setLong(3,entity.getId().getRight());
            psUpdate.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return oldState;
    }
}
